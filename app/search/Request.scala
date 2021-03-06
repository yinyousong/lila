package lila
package search

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.ActionRequest
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query._, FilterBuilders._, QueryBuilders._
import org.elasticsearch.search._, facet._, terms._, sort._, SortBuilders._, builder._

import scalastic.elasticsearch.{ Indexer ⇒ EsIndexer }
import scalastic.elasticsearch.SearchParameterTypes

import Game.fields._

case class SearchRequest(
    query: QueryBuilder,
    filter: Option[FilterBuilder] = None,
    size: Int = 10,
    from: Int = 0,
    sortings: Iterable[SearchParameterTypes.Sorting] = Nil) {

  val explain = none[Boolean]

  def in(indexName: String, typeName: String)(es: EsIndexer): SearchResponse =
    es.search(Seq(indexName), Seq(typeName), query,
      filter = filter,
      sortings = sortings,
      from = from.some,
      size = size.some,
      explain = explain
    )
}

case class CountRequest(
    query: QueryBuilder,
    filter: Option[FilterBuilder] = None) {

  def in(indexName: String, typeName: String)(es: EsIndexer): Int = {
    es.search(Seq(indexName), Seq(typeName), query,
      filter = filter,
      searchType = SearchType.COUNT.some
    )
  }.hits.totalHits.toInt
}
