package cc.timeli.algebra.user

import cats.effect.Concurrent
import cats.data.EitherT
import org.typelevel.log4cats.{Logger, LoggerFactory}
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

import cc.timeli.core.domain.user.*
import cc.timeli.core.domain.role.*
import cc.timeli.core.errors.BaseError
import cc.timeli.algebra.user.userDtos.*
import cc.timeli.core.errors.baseErrors.*
import cc.timeli.core.logging.syntax.*

trait UserAlgebra[F[_]] {
  def me(meDto: MeDto): EitherT[F, BaseError, MeData]
  def userMeta: EitherT[F, BaseError, UserMetaData]
  def userTable(tableDto: UserTableDto): EitherT[F, BaseError, UserTableData]
}

final class UserAlgebraLive[F[_]: Concurrent: LoggerFactory](
    session: Session[F],
) extends UserAlgebra[F] {
  given logger: Logger[F] = LoggerFactory.getLogger()

  override def me(meDto: MeDto): EitherT[F, BaseError, MeData] = {
    for {
      userQuery <- EitherT.right(
        session.prepare(
          sql"""SELECT id, email, password, firstName, lastName, status FROM users WHERE id = $uuid""".query(userCodec),
        ),
      )
      user <- EitherT.fromOptionF(userQuery.option(meDto.id), NotFoundError("User could not be found"))
    } yield MeData(
      id = user.id,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      status = user.status,
    )
  }

  override def userMeta: EitherT[F, BaseError, UserMetaData] = {
    for {
      rolesQuery <- EitherT.right(
        session
          .prepare(
            sql"""SELECT r.id, r.name, r.label, r.color FROM roles r WHERE r.name NOT IN ('ADMIN','SUPERADMIN')"""
              .query(roleCodec),
          ),
      )
      roles <- EitherT.right(rolesQuery.stream(Void, 64).compile.toList)
    } yield UserMetaData(
      roles,
    )
  }

  override def userTable(tableDto: UserTableDto): EitherT[F, BaseError, UserTableData] =
    for {
      userTableQuery <- EitherT
        .right(
          session.prepare(sql"""
          WITH 
          usersFilteredData AS (
            SELECT 
            u.id,
            u.email,
            u.password,
            u.firstName AS "firstName",
            u.lastName AS "lastName",
            u.status,
            jsonb_build_object(
              'id', r.id,
              'name', r.name,
              'label', r.label,
              'color', r.color,
              'mask', r.mask
            ) AS role
            FROM users u
            JOIN roles r ON u.roleId = r.id
            WHERE (
              (r.name NOT IN ('SUPERADMIN','ADMIN'))
              AND (${varchar.opt} IS NULL 
              OR (
                  u.email ILIKE '%' || ${varchar.opt} || '%'
                  OR u.firstName ILIKE '%' || ${varchar.opt} || '%'
                  OR u.lastName ILIKE '%' || ${varchar.opt} || '%'
                )
              )
              AND (${varchar.opt} IS NULL OR r.name = ${varchar.opt})
              AND (${varchar.opt} IS NULL OR u.status = ${varchar.opt})
            )
            ORDER BY u.lastName ASC, u.firstName ASC
            ),
            usersFilteredCount AS (
              SELECT COUNT(*) FROM usersFilteredData
            ), 
            usersPaginatedData AS (
              SELECT *
              FROM usersFilteredData 
              LIMIT $int4 OFFSET $int4
            )
            SELECT jsonb_build_object(
              'rowCount', (SELECT count FROM usersFilteredCount),
              'rowData', COALESCE((SELECT jsonb_agg(to_jsonb(up)) FROM usersPaginatedData up), '[]'::jsonb)
            ) AS userTableData
            """.query(userTableCodec)),
        )
        .logError(_.toString)
      userTable <- EitherT
        .fromOptionF[F, BaseError, UserTable](
          userTableQuery.option(
            tableDto.searchTerm,
            tableDto.searchTerm,
            tableDto.searchTerm,
            tableDto.searchTerm,
            tableDto.fRole,
            tableDto.fRole,
            tableDto.fStatus.map(_.value),
            tableDto.fStatus.map(_.value),
            tableDto.pageSize,
            tableDto.pageIndex * tableDto.pageSize,
          ),
          UnexpectedQueryResult("Unexpected error with userTable query"),
        )
        .logError(_.toString)
    } yield UserTableData(
      rowCount = userTable.rowCount,
      rowData = userTable.rowData,
    )
}

object UserAlgebraLive {
  def apply[F[_]: Concurrent: LoggerFactory](session: Session[F]) = new UserAlgebraLive(session)
}
