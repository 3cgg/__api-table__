package scalalg.me.libme.apitable.dic

import me.libme.kernel._c.util.CliParams
import me.libme.xstream.excel.SimpleExcelSource
import me.libme.xstream.{SourceMeta, Sourcer}

import scala.collection.JavaConversions

/**
  * Created by J on 2018/2/8.
  */
object SimpleExcelSourcerFactory extends SourcerFactory{

  override def factory(conf: Map[String, Object]): Sourcer = {

    val cliParams:CliParams=new CliParams(JavaConversions.mapAsJavaMap(conf))

    val name:String=cliParams.getString("name")
    val sheetName=cliParams.getString("sheet")
    val file=cliParams.getString("file")

    val sourceMeta:SourceMeta=new SourceMeta(name)

    val simpleExcelSource:SimpleExcelSource=new SimpleExcelSource(file,sheetName,sourceMeta)

    return simpleExcelSource

  }

}
class SimpleExcelSourcerFactory{}
