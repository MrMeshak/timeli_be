package cc.timeli.algebra.booking

import scala.math.BigInt

import cats.effect.Concurrent
import cats.data.{EitherT, OptionT}
import cats.implicits.*
import cats.syntax.*
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*
import org.typelevel.log4cats.{LoggerFactory, Logger}

import cc.timeli.core.logging.syntax.*
import cc.timeli.core.errors.BaseError
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.algebra.booking.bookingDtos.*

import java.time.DayOfWeek

import cc.timeli.core.domain.room.*
import cc.timeli.core.domain.roomType.*

trait BookingAlgebra[F[_]] {
  def bookingContext: EitherT[F, BaseError, BookingContextData]
  def bookingMatrix(bookingMatrixDto: BookingMatrixDto): EitherT[F, BaseError, BookingMatrixData]

}

final class BookingAlgebraLive[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
) extends BookingAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def bookingContext: EitherT[F, BaseError, BookingContextData] = {
    for {
      roomTypesQuery <- EitherT.right(session.prepare(sql"""
        SELECT id, name FROM roomTypes;  
        """.query(roomTypeCodec)))
      roomTypes <- EitherT.right(roomTypesQuery.stream(Void, 64).compile.toList)
    } yield {
      BookingContextData(roomTypes)
    }
  }

  override def bookingMatrix(bookingMatrixDto: BookingMatrixDto): EitherT[F, BaseError, BookingMatrixData] = {
    for {
      roomsQuery <- EitherT
        .right[BaseError](
          session
            .prepare(
              sql"""
              WITH 
              pricePoliciesData AS (
                SELECT roomId,
                jsonb_agg(
                  jsonb_build_object(
                    'id', id,
                    'startDate', startDate,
                    'dayOfWeek', dayOfWeek,
                    'price', price,
                    'mask', mask,
                    'roomId', roomId
                  )
                ) AS pricePolicies
                FROM (
                  SELECT DISTINCT ON (roomId, dayOfWeek) *
                  FROM pricePolicies
                  WHERE dayOfWeek = $int4 AND startDate <= $date
                  ORDER BY roomId, dayOfWeek, startDate DESC
                ) latestPricePolicies
                GROUP BY roomId
              ),
              availablityData AS (
                SELECT roomId,
                jsonb_agg(
                  jsonb_build_object(
                    'id', id,
                    'startDate', startDate,
                    'dayOfWeek', dayOfWeek,
                    'mask', mask,
                    'roomId', roomId
                  )
                ) AS availability
                FROM (
                  SELECT DISTINCT ON (roomId, dayOfWeek) *
                  FROM availability
                  WHERE dayOfWeek = $int4 AND startDate <= $date
                  ORDER BY roomId, dayOfWeek, startDate DESC
                ) latestAvailability
                GROUP BY roomId
              ),
              slotsData AS (
                SELECT s.roomId,
                jsonb_agg(
                  jsonb_build_object(
                    'id', s.id,
                    'slotDate', s.slotDate,
                    'slotIndex', s.slotIndex,
                    'status', s.status,
                    'roomId', s.roomId,
                    'bookingId', s.bookingId,
                    'userId', s.userId
                  )
                ) AS slots
                FROM slots s
                WHERE s.slotDate = $date
                GROUP BY s.roomId
              )
              SELECT
                r.id,
                r.name,
                r.displayName,
                r.roomCode,
                r.description,
                r.capacity,
                r.slotsize,
                r.defaultPrice,
                jsonb_build_object(
                  'id', rt.id, 
                  'name', rt.name
                ) AS roomType,
                jsonb_build_object(
                  'id', l.id,
                  'name', l.name,
                  'description', l.description,
                  'street', l.street,
                  'city', l.city,
                  'state', l.state,
                  'country', l.country,
                  'postCode', l.postCode
                ) as location,
                COALESCE(ad.availability, '[]') AS availability,
                COALESCE(ppd.pricePolicies, '[]') AS pricePolicies,
                COALESCE(sd.slots, '[]') AS slots
              FROM rooms r
              JOIN roomTypes rt ON r.roomTypeId = rt.id
              JOIN locations l ON r.locationId = l.id
              LEFT JOIN availablityData ad ON ad.roomId = r.id
              LEFT JOIN pricePoliciesData ppd ON ppd.roomId = r.id
              LEFT JOIN slotsData sd ON sd.roomId = r.id
              WHERE r.roomTypeId = $varchar;
            """.query(roomWithDetailsDecoder),
            ),
        )
        .log(l => l.toString(), r => r.toString(), e => e.getMessage)
      rooms <- EitherT
        .right[BaseError](
          roomsQuery
            .stream(
              (
                bookingMatrixDto.date.getDayOfWeek.getValue - 1,
                bookingMatrixDto.date,
                bookingMatrixDto.date.getDayOfWeek.getValue - 1,
                bookingMatrixDto.date,
                bookingMatrixDto.date,
                bookingMatrixDto.roomTypeId,
              ),
              64,
            )
            .compile
            .toList,
        )
        .log(l => l.toString, r => r.toString, e => e.getMessage)
    } yield {

      val bookingRooms = rooms.map(r => {
        val availabilityMask  = r.availability.lift(0).map(_.mask).getOrElse(BigInt(0))
        val pricePoliciesMask = r.pricePolicies.lift(0).map(_.mask).getOrElse(BigInt(0))
        val slotMask          = r.slots.foldLeft(BigInt(0))((z, s) => BigInt(1) << s.slotIndex | z)

        val slots = List.tabulate(24 * 60 / r.slotSize)(i => {

          val status =
            if ((BigInt(1) << i & slotMask) != 0) BookingSlotStatus.BOOKED
            else if ((BigInt(1) << i & availabilityMask) != 0) BookingSlotStatus.AVAILABLE
            else BookingSlotStatus.UNAVAILABLE

          val price =
            if ((BigInt(1) << i & pricePoliciesMask) != 0)
              r.pricePolicies.lift(0).map(_.price).getOrElse(r.defaultPrice)
            else r.defaultPrice

          BookingSlot(i, status, price, startMin = r.slotSize * i)
        })

        BookingRoom(r.id, r.name, r.displayName, r.slotSize, r.capacity, slots)
      })

      BookingMatrixData(
        BookingMatrix(
          bookingRooms,
        ),
      )
    }
  }
}

object BookingAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](
      session: Session[F],
  ) = new BookingAlgebraLive(session)
}
