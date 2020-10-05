package app.models

import sangria.execution.deferred.HasId

trait Identifiable[T] {def id: T}

object Identifiable {
  implicit def hasId[S, T <: Identifiable[S]]: HasId[T, S] = HasId(_.id)
}
