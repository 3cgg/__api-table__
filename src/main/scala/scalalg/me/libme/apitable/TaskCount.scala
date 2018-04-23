package scalalg.me.libme.apitable

/**
  * Created by J on 2018/4/23.
  */
class TaskCount(var count: Int) {



  def plus(): Unit ={
    this.synchronized{
      count=count+1
    }
  }

  def reduce(): Unit ={

    this.synchronized{
      count=count-1
      exit()
    }

  }


  private def exit(): Unit ={

    if(count==0){
      Runtime.getRuntime.exit(0)
    }

  }






}
