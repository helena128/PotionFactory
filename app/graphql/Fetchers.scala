package graphql

import sangria.execution.deferred.Fetcher
import security.AppContext

trait Fetchers[Res, RelRes, Id] {
  val fetchers: Seq[Fetcher[AppContext, Res, RelRes, Id]]
  def apply(): Seq[Fetcher[AppContext, Res, RelRes, Id]] = fetchers
}
