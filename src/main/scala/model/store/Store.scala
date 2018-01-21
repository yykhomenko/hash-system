package model.store

import java.util.UUID

import model.E164Format

import scala.collection.mutable

abstract class Store extends E164Format {

  val hashes = Array.ofDim[UUID](ndcs.size, ndcNums)
  val msisdns = new mutable.HashMap[UUID, Int] {
    override def initialSize = Math.round(ndcs.size * ndcNums * 1.5).toInt
  }

  def storeNewHashes()
  def loadHashes()
}