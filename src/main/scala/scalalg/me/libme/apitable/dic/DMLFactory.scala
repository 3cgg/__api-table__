package scalalg.me.libme.apitable.dic

import me.libme.kernel._c.util.CliParams
import me.libme.xstream.EntryTupe.Entry
import me.libme.xstream.{Consumer, ConsumerMeta, Tupe}
import me.libme.xstream.excel.ExcelCompositer

import scala.collection.JavaConversions

/**
  * Created by J on 2018/2/8.
  */
object DMLFactory extends ConsumerFactory{



  override def factory(conf: Map[String, Object]): Consumer = {


    val cliParams:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))


    val name:String=cliParams.getString("name")
//    val mappings:Map[String,String]=classOf[Map[String,String]].cast(cliParams.getObject("mapping"))


    val consumerMeta=new ConsumerMeta(name)

    return new DMLConsumer(conf,consumerMeta)



  }




}

class DMLConsumer(conf:Map[String,AnyRef],consumerMeta: ConsumerMeta) extends ExcelCompositer(consumerMeta){


  val dmlInsert:String=classOf[String].cast(conf.get("dmlInsert"))

  override def doPrepare(tupe: Tupe[_]): Unit = {

  }


  def mapping(tupe: Tupe[_],entry:Entry):Unit={

    mappings.get(entry.getKey) match {
      case Some(value)=>produce(new Entry(value,entry.getValue))
      case None =>

    }



  }

  override def doConsume(tupe: Tupe[_]): Unit = {

    val iterator:Iterator[_]=JavaConversions.asScalaIterator(tupe.iterator());
    while(iterator.hasNext){
      mapping(tupe,classOf[Entry].cast(iterator.next()))
    }

  }


}


























