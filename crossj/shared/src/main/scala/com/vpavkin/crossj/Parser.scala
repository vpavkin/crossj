package com.vpavkin.crossj

trait Parser {
  def parse(s: String): ReadResult[JSON]
}
