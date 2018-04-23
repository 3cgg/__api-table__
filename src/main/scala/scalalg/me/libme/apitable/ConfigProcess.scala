package scalalg.me.libme.apitable

import java.util.Optional
import java.util.concurrent.{Executors, ThreadFactory}

import me.libme.kernel._c.file.FileTransferCfg
import me.libme.kernel._c.json.JJSON
import me.libme.kernel._c.util.CliParams
import me.libme.xstream._
import me.libme.xstream.excel.{ExcelCompositer, SimpleExcelSource}

import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

/**
  * Created by J on 2018/2/9.
  */
object ConfigProcess {

  val conifgFile_k="--config.files"
  val logRepo_k="--log.repo"

  val taskCount=new TaskCount(0);

  def main(args: Array[String]): Unit = {

    import scala.collection.JavaConversions._

    val cliParams:CliParams=new CliParams(args)

    val files=cliParams.getString(conifgFile_k," ")

    val logRepo=Optional.of(cliParams.getString(logRepo_k)).orElse("d:/test-xml-streaming")
    val fileTransferCfg = new FileTransferCfg
    fileTransferCfg.setDskPath(logRepo)
    val repository = new FileRepository(fileTransferCfg)

    for(file <- files){

      val content=Source.fromFile(file,"utf-8").mkString
      val conf=JavaConversions.mapAsScalaMap(JJSON.get().parse(content))

      // source

      val sourceConfig:scala.collection.mutable.Map[String,Object]=classOf[java.util.Map[String,Object]].cast(conf.get("source").get)
      val sourceCliParam=new CliParams(JavaConversions.mapAsJavaMap(sourceConfig))
      val factory=sourceCliParam.getString("factory")
      var source:Sourcer=null
      if(factory.equals(classOf[SimpleExcelSourcerFactory].getName)){
        val _source:SimpleExcelSource=classOf[SimpleExcelSource].cast(SimpleExcelSourcerFactory.factory(sourceConfig))
        _source.setRepository(repository)
        source=_source
      }

      val consumeConfgs=classOf[java.util.List[java.util.Map[String,Object]]].cast(conf.get("consume").get)
      val consumers=new ArrayBuffer[Consumer](consumeConfgs.length)

      //consume
      for(consumeConfg <- consumeConfgs){
        val consumerCliParam=new CliParams(JavaConversions.mapAsJavaMap(consumeConfg))
        val factory=consumerCliParam.getString("factory")
        if(factory.equals(classOf[MappingFactory].getName)){
          val consume:ExcelCompositer=classOf[ExcelCompositer].cast(MappingFactory.factory(consumeConfg))
          consume.setRepository(repository)
          consumers.append(consume)
        }else if(factory.equals(classOf[DMLFactory].getName)){
          val consume:ExcelCompositer=classOf[ExcelCompositer].cast(DMLFactory.factory(consumeConfg))
          consume.setRepository(repository)
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

      val thread=new Thread(new Runnable {
        override def run(): Unit = {
          //out
          val output=new Output(classOf[java.util.Map[String,Object]].cast(conf.get("out").get),taskCount)
          output.output()
        }
      },"out:"+file)
      thread.setDaemon(true)
      thread.start()

      taskCount.plus();
    }


  }



}
