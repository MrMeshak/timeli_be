package cc.timeli.core.db.seeder

import cats.effect.{IOApp, IO}
import cats.effect.kernel.Resource
import cats.syntax.*
import cats.implicits.*
import pureconfig.ConfigSource
import natchez.Trace.Implicits.noop
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

import java.util.UUID

import cc.timeli.core.config.syntax.*
import cc.timeli.core.config.DbConfig
import cc.timeli.core.db.Db
import cc.timeli.core.domain.role.*
import cc.timeli.core.domain.user.*
import cc.timeli.core.domain.location.*
import cc.timeli.core.domain.roomType.*
import cc.timeli.core.domain.room.*
import cc.timeli.core.domain.availability.*
import cc.timeli.core.domain.pricePolicy.*
import cc.timeli.core.domain.booking.*
import cc.timeli.core.domain.slot.*

import cc.timeli.core.config.SeederConfig
import cc.timeli.core.db.seeder.data.roleSeedData
import cc.timeli.core.db.seeder.data.locationSeedData
import cc.timeli.core.db.seeder.data.roomTypeSeedData
import cc.timeli.core.db.seeder.data.roomSeedData
import cc.timeli.core.db.seeder.data.userSeedData
import cc.timeli.core.db.seeder.data.bookingSeedData
import cc.timeli.core.db.seeder.data.availabilitySeedData
import cc.timeli.core.db.seeder.data.pricePolicyData
import cc.timeli.core.db.seeder.data.slotSeedData

object Seeder extends IOApp.Simple {

  override def run: IO[Unit] = {

    val sessionR = for {
      dbConfig <- Resource.eval(ConfigSource.default.at("db").loadF[IO, DbConfig])
      session  <- Db.single[IO](dbConfig)
    } yield session

    sessionR.use(session => {
      for {
        seederConfig <- ConfigSource.default.at("seeder").loadF[IO, SeederConfig]

        commandRole <- session.prepare(sql"""
        INSERT INTO roles VALUES ($roleCodec)
        ON CONFLICT(id) DO UPDATE SET 
          name = EXCLUDED.name,
          mask = EXCLUDED.mask
       """.command)
        _ <- roleSeedData.traverse(r => commandRole.execute(r))

        commandUser <- session.prepare(sql"""
          INSERT INTO users VALUES ($userCodec, DEFAULT, (SELECT id FROM roles WHERE name = 'USER'))
          ON CONFLICT(id) DO NOTHING
          """.command)
        _ <- userSeedData(seederConfig).traverse(u => commandUser.execute(u))

        commandLocation <- session.prepare(sql"""
        INSERT INTO locations VALUES ($locationCodec)
        ON CONFLICT DO NOTHING
        """.command)
        _ <- locationSeedData.traverse(l => commandLocation.execute(l))

        commandRoomType <- session.prepare(sql"""
          INSERT INTO roomTypes VALUES ($roomTypeCodec)
          ON CONFLICT DO NOTHING
          """.command)
        _ <- roomTypeSeedData.traverse(rt => commandRoomType.execute(rt))

        commandRoom <- session.prepare(sql"""
          INSERT INTO rooms VALUES ($roomCodec) 
          ON CONFLICT DO NOTHING
          """.command)
        _ <- roomSeedData.traverse(r => commandRoom.execute(r))

        commandAvailability <- session.prepare(sql"""
          INSERT INTO availability VALUES ($availabilityCodec)
          ON CONFLICT DO NOTHING
          """.command)
        _ <- availabilitySeedData.traverse(a => commandAvailability.execute(a))

        commandPricePolicy <- session.prepare(sql"""
          INSERT INTO pricePolicies VALUES ($pricePolicyCodec) 
          ON CONFLICT DO NOTHING 
          """.command)
        _ <- pricePolicyData.traverse(p => commandPricePolicy.execute(p))

        commandBooking <- session.prepare(sql"""
          INSERT INTO bookings VALUES ($bookingCodec)
          ON CONFLICT DO NOTHING
          """.command)
        _ <- bookingSeedData.traverse(b => commandBooking.execute(b))

        commandSlot <- session.prepare(sql"""
          INSERT INTO slots VALUES ($slotCodec) 
          ON CONFLICT DO NOTHING
          """.command)
        _ <- slotSeedData.traverse(s => commandSlot.execute(s))

      } yield ()
    })
  }
}
