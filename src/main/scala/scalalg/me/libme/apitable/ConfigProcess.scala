package scalalg.me.libme.apitable

import java.util.concurrent.{ExecutorService, Executors, ThreadFactory}

import me.libme.kernel._c.json.JJSON
import me.libme.kernel._c.util.CliParams
import me.libme.xstream.{Consumer, SimpleTopology}

import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scalalg.me.libme.apitable.dic.{DMLFactory, MappingFactory, SimpleExcelSourcerFactory}

/**
  * Created by J on 2018/2/9.
  */
object ConfigProcess {

  val conifgFile="--config.files"

  def main(args: Array[String]): Unit = {

    val cliParams:CliParams=new CliParams(args)

    val files=cliParams.getString(conifgFile," ")

    for(file <- files){

      val content=Source.fromFile(file,"utf-8").mkString
      val conf=JavaConversions.mapAsScalaMap(JJSON.get().parse(content))

      // source
      val sourceConfig=classOf[Map[String,Object]].cast(conf.get("source"))
      val sourceCliParam=new CliParams(JavaConversions.mapAsJavaMap(sourceConfig))
      val factory=sourceCliParam.getString("factory")
      var source=_
      if(factory.equals(classOf[SimpleExcelSourcerFactory].getName)){
        source=SimpleExcelSourcerFactory.factory(sourceConfig)
      }

      val consumeConfgs=classOf[List[Map[String,Object]]].cast(conf.get("consume"))
      val consumers=new ArrayBuffer[Consumer](consumeConfgs.length)

      //consume
      for(consumeConfg <- consumeConfgs){
        val consumerCliParam=new CliParams(JavaConversions.mapAsJavaMap(consumeConfg))
        val factory=consumerCliParam.getString("factory")
        if(factory.equals(classOf[MappingFactory].getName)){
          val consume=MappingFactory.factory(consumeConfg)
          consumers.append(consume)
        }else if(factory.equals(classOf[DMLFactory].getName)){
          val consume=DMLFactory.factory(consumeConfg)
          consumers.append(consume)
        }
      }


      val simpleTopology=new SimpleTopology(file,Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors(),new ThreadFactory {
        override def newThread(r: Runnable): Thread = {
          new Thread(r,file)
        }
      }))
      simpleTopology.setSourcer(source)
      consumers.foreach(c=>simpleTopology.addConsumer(c))
      simpleTopology.start()

      new Thread()

      //out
      val output=new Output(classOf[Map[String,Object]].cast(conf.get("out")))
      output.output()



    }


  }



}
