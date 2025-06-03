package cc.timeli.algebra.booking

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
import cc.timeli.core.domain.room.*

trait BookingAlgebra[F[_]] {
  def bookingMatrix(bookingMatrixDto: BookingMatrixDto): EitherT[F, BaseError, List[RoomWithDetails]]
}

final class BookingAlgebraLive[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
) extends BookingAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def bookingMatrix(bookingMatrixDto: BookingMatrixDto): EitherT[F, BaseError, List[RoomWithDetails]] = {
    for {
      _ <- EitherT.right[BaseError](logger.info("Booking Matrix Route"))
      roomsQuery <- EitherT
        .right[BaseError](
          session
            .prepare(
              sql"""
              WITH 
              pricePoliciesData AS (
                SELECT pp.roomId,
                jsonb_agg(
                  jsonb_build_object(
                    'id', pp.id,
                    'dayOfWeek', pp.dayOfWeek,
                    'price', pp.price,
                    'mask', pp.mask,
                    'roomId', pp.roomId
                  )
                ) AS pricePolicies
                FROM pricePolicies pp
                GROUP BY pp.roomId
              ),
              availablityData AS (
                SELECT av.roomId,
                jsonb_agg(
                  jsonb_build_object(
                    'id', av.id,
                    'dayOfWeek', av.dayOfWeek,
                    'mask', av.mask,
                    'roomId', av.roomId
                  )
                ) AS availability
                FROM availability av
                GROUP BY av.roomId
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
                ),
                COALESCE(ad.availability, '[]') AS availablity,
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
        .right[BaseError](roomsQuery.stream((bookingMatrixDto.date, bookingMatrixDto.roomTypeId), 64).compile.toList)
        .log(l => l.toString, r => r.toString, e => e.getMessage)
    } yield rooms
  }
}

object BookingAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](
      session: Session[F],
  ) = new BookingAlgebraLive(session)
}
