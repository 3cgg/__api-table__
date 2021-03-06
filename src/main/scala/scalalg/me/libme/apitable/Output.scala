package scalalg.me.libme.apitable

import java.io
import java.io.{BufferedWriter, File}
import java.util.Date
import java.util.concurrent.TimeUnit

import me.libme.kernel._c.pubsub._
import me.libme.kernel._c.util.{CliParams, JDateUtils, JStringUtils, JUniqueUtils}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions
import scala.io.{Codec, Source}

/**
  * Created by J on 2018/2/9.
  */
class Output(conf:collection.mutable.Map[String,AnyRef],taskCount: TaskCount) {

  val LOGGER:Logger=LoggerFactory.getLogger(classOf[Output])


  val topic:String=classOf[String].cast(conf.get("topic").get)
  val subscriber:Subscriber=new Subscriber(new Topic(topic),QueuePools.defaultPool())
  val consumer:Consume=subscriber.consume()
  val filePath=classOf[String].cast(conf.get("file").get)


  def backup():Unit={

    val file=new File(filePath)
    if(file.exists()){
      val newName=file.getAbsolutePath+"."+JUniqueUtils.sequence()
      file.renameTo(new File(newName))
    }

  }


  def appendChar(c:String):Unit={

    var bw:Option[BufferedWriter]=None

    try{
      bw=Some(new BufferedWriter(new io.FileWriter(filePath,true)))
      bw.get.append(c)
      bw.get.flush()
    }catch {
      case e:Exception =>{
        throw new RuntimeException(e)
      }

    }finally {
      bw.foreach(f=>f.close())
    }
  }

  def output():Unit={

    backup

    val cliParam:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))
    val ddl=cliParam.getString("ddl")
    val ddlFile=cliParam.getString("ddlFile")
    var content=ddl
    if(JStringUtils.isNullOrEmpty(content)){
      content=Source.fromFile(ddlFile,"utf-8").mkString
    }
    appendChar(content);  // write DDL statement



    var stringBuffer=new StringBuffer()
    val latestTime=System.currentTimeMillis()


    def accept(latestTime:Long):Boolean={
      val elapse=System.currentTimeMillis()-latestTime
      if(elapse>1000*30){ //30s
        return false
      }
      var isConsume=false
      if(consumer.hasNext){
        isConsume=true
        while(consumer.hasNext){
          stringBuffer.append(classOf[String].cast(consumer.next()))
          if(stringBuffer.length>5000){
            appendChar(stringBuffer.toString)
            stringBuffer=new StringBuffer()
          }
        }
      }

      accept(if(isConsume) System.currentTimeMillis() else {
        LOGGER.info(Thread.currentThread().getName+" had not consume data since : "+JDateUtils.formatWithMSeconds(new Date(latestTime)))
        TimeUnit.SECONDS.sleep(3)
        latestTime
      })

    }

    while(accept(latestTime)){}

    if(stringBuffer.length()>0){
      appendChar(stringBuffer.toString)
    }

    LOGGER.info(Thread.currentThread().getName+" ready to exit");
    taskCount.reduce();
  }






}
