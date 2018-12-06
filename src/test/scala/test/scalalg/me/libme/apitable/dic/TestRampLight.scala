package test.scalalg.me.libme.apitable.dic

import java.io.FileInputStream

import me.libme.xstream.stream.JSONFileStreamBuilder

/**
  * Created by J on 2018/12/6.
  */
object TestRampLight {


  def main(args: Array[String]): Unit = {

    val builder = new JSONFileStreamBuilder
    val stream = builder.build(new FileInputStream("C:\\java_\\git\\__api-table__\\src\\main\\resources\\abc\\stream-abc-ramp-light.json"))
    stream.start()

    println("")


  }


}
