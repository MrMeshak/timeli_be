package cc.timeli.algebra.room

import cats.effect.Concurrent
import cats.data.EitherT
import cats.implicits.*
import cats.syntax.*
import org.typelevel.log4cats.{Logger, LoggerFactory}
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

import cc.timeli.core.errors.BaseError
import cc.timeli.core.domain.room.*
import cc.timeli.algebra.room.roomDtos.*
import cc.timeli.core.logging.syntax.*
import cc.timeli.core.domain.room.RoomWithTAP_S

trait RoomAlgebra[F[_]] {
  def roomGrid(roomGridDto: RoomGridDto): EitherT[F, BaseError, RoomGridData]
}

final class RoomAlgebraLive[F[_]: Concurrent: LoggerFactory](session: Session[F]) extends RoomAlgebra[F] {
  given Logger[F] = LoggerFactory.getLogger()

  override def roomGrid(roomGridDto: RoomGridDto): EitherT[F, BaseError, RoomGridData] =
    for {
      roomsQuery <- EitherT
        .right(session.prepare(sql"""
      WITH
      availabilityPolicyData AS (
        SELECT DISTINCT ON (ap.roomId)
        ap.roomId,
        jsonb_build_object(
          'id', ap.id,
          'startDate', ap.startDate,
          'policy', ap.policy,
          'roomId', ap.roomId
        ) AS availabilityPolicy
        FROM availabilityPolicies ap
        WHERE ap.startDate <= $date
        ORDER BY ap.roomId, ap.startdate DESC
      ),
      availabilityPolicyByDate AS (
        SELECT 
        apbd.roomId,
        jsonb_build_object(
          'id', apbd.id,
          'activeDate', apbd.activeDate,
          'policy', apbd.policy,
          'roomId', apbd.roomId
        ) AS availabilityPolicyByDate
        FROM availabilitypoliciesbydate apbd
        WHERE apbd.activedate = $date
      ),
      pricePolicyData AS (
        SELECT DISTINCT ON (pp.roomId)
        pp.roomId,
        jsonb_build_object(
          'id', pp.id,
          'startDate', pp.startDate,
          'policy', pp.policy,
          'roomId', pp.roomId
        ) as pricePolicy
        FROM pricePolicies pp
        WHERE pp.startDate <= $date
        ORDER BY pp.roomId, pp.startDate DESC
      ),
      pricePolicyByDateData AS (
        SELECT
        ppbd.roomId,
        jsonb_build_object(
          'id', ppbd.id,
          'activeDate', ppbd.activeDate,
          'policy', ppbd.policy,
          'roomId', ppbd.roomId
        ) AS pricePolicyByDate
        FROM pricePoliciesByDate ppbd
        WHERE ppbd.activeDate = $date
      ),
      bookingSlotData AS (
        SELECT 
        bs.roomId,
        jsonb_agg(
          jsonb_build_object(
            'id', bs.id,
            'slotDate', bs.slotDate,
            'slotIndex', bs.slotIndex,
            'status', bs.status,
            'roomId', bs.roomId,
            'bookingId', bs.bookingId,
            'userId', bs.userId,
            'user' , jsonb_build_object(
              'id', u.id,
              'email', u.email,
              'password', '',
              'firstName', u.firstName,
              'lastName', u.lastName,
              'status', u.status,
              'role', jsonb_build_object(
                'id', r.id,
                'name', r.name,
                'label', r.label,
                'color', r.color, 
                'mask', r.mask
              ) 
            )
          ) ORDER BY bs.slotindex
        ) AS bookingSlots
        FROM bookingSlots bs 
        JOIN users u ON u.id = bs.userId
        JOIN roles r ON r.id = u.roleId
        WHERE bs.slotdate = $date 
        AND (bs.status NOT IN ('CANCELED'))
        GROUP BY bs.roomid
      )
      SELECT 
      r.id ,
      r.name,
      r.displayname,
      r.roomcode,
      r.description,
      r.capacity,
      r.slotsize,
      r.roomTypeId,
      r.locationId,
      jsonb_build_object(
        'id', rt.id,
        'name', rt.name
      ) AS roomType,
      ap.availabilityPolicy,
      apbd.availabilityPolicyByDate,
      pp.pricePolicy,
      ppbd.pricePolicyByDate,
      COALESCE(bs.bookingSlots, '[]') AS bookingSlots
      FROM rooms r  
      JOIN roomTypes rt ON rt.id = r.roomtypeid
      JOIN availabilityPolicyData ap ON ap.roomId = r.id
      LEFT JOIN availabilityPolicyByDate apbd ON apbd.roomId = r.id
      JOIN pricePolicyData pp ON pp.roomId = r.id
      LEFT JOIN pricePolicyByDateData ppbd ON ppbd.roomId = r.id
      LEFT JOIN bookingSlotData bs ON bs.roomId = r.id
      """.query(roomWithTAP_SCodec)))
        .logError(_.toString)
      rooms <- EitherT
        .right(
          roomsQuery
            .stream(
              (
                roomGridDto.date,
                roomGridDto.date,
                roomGridDto.date,
                roomGridDto.date,
                roomGridDto.date,
              ),
              64,
            )
            .compile
            .toList,
        )
        .logError(_.toString)
    } yield RoomGridData(
      rooms,
    )
}

object RoomAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](session: Session[F]) =
    new RoomAlgebraLive[F](session)
}
