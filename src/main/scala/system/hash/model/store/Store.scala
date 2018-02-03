package system.hash.model.store

import java.util.UUID

import system.hash.model.E164Format

import scala.collection.mutable

trait Store extends E164Format {

  val hashes = Array.ofDim[UUID](ndcs.size, ndcNums)
  val msisdns = new mutable.HashMap[UUID, Int] {
    override def initialSize = Math.round(ndcs.size * ndcNums * 1.5).toInt
  }

  hashes(0)(2244089) = UUID.fromString("4401657f-fba5-454e-bc4a-5d34d09d1548")

  def storeNewHashes()
  def loadHashes()
}