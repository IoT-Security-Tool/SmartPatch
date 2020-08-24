import groovy.json.JsonSlurper
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook


OpPath="../"
addfile = new File(OpPath+"configFile/app_device.groovy")
headfile = new File(OpPath+"configFile/Head.groovy")
headfile_sun = new File(OpPath+"configFile/app_header_sunrise_sunset.groovy")

ClassLoader parent = getClass().getClassLoader();
GroovyClassLoader loader = new GroovyClassLoader(parent);

candir = new File("../sourceFile/testDevice")

List filelist=[]
candir.eachFile{filepath->
    if(filepath.isFile())
        if(filepath.name!=".DS_Store")
            filelist.add(filepath.toString())
}

//record time
createExcel()

/*The configuration file generation path is in the MyASTTransformation
Default is in allconfig*/
for (int i = 0; i < filelist.size(); i++) {
    Class bClass = loader.parseClass(new File(filelist[i]))
    GroovyObject Object = (GroovyObject)bClass.newInstance()

}

entire = new adddevfunc();

if (candir !=null && candir.exists()&& candir.isDirectory()){
    File[] files = candir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){

            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            filepath = "../sourceFile/testDevice/"+filename+".groovy"
            sourcefile = new File(filepath)
            configpath =  "../configFile/allconfig/"+filename+"_config.txt"
            configfile = new File(configpath)
            outputpath = "../outFile/out_can_device/"+filename+"_out.txt"
            outfile = new File(outputpath)

            if(outfile.exists() && outfile.isFile()){
                outfile.delete()
            }
            outfile.createNewFile()
            methodMap=[:]

            createEventlist=[]
            sendEventlist=[]

            createEventnumberlist=[]
            sendEventnumberlist=[]
            configlist = configfile.collect {it}
            slurper = new JsonSlurper()

            entire.getconfig(configlist,slurper,methodMap,sendEventlist,createEventlist,createEventnumberlist,sendEventnumberlist)
            entire.add(filename,headfile,sourcefile,methodMap,slurper,createEventnumberlist,sendEventnumberlist,createEventlist,sendEventlist,outfile,i+1)

            println(filename+" done")
            println()
        }
    } else{
        println("no file exists")
    }
}
else {
    println("there is no such directory")
}

def createExcel(){

    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelTest.xls"
    String sheetName = "ExcelContentSheet"
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook()
    HSSFSheet excelSheet = hssfWorkbook.createSheet(sheetName)
    excelSheet.setDefaultColumnWidth(50)
    HSSFRow hssfRow = null
    HSSFCell hssfCell = null

    FileOutputStream fileOutputStream = new FileOutputStream(excelFileName)
    hssfWorkbook.write(fileOutputStream)
}
