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

candir = new File("../sourceFile/testSmartAPP")

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
    long startTime = System.currentTimeMillis();
    Class bClass = loader.parseClass(new File(filelist[i]))
    GroovyObject Object = (GroovyObject)bClass.newInstance()
    long endTime = System.currentTimeMillis();
    long parseTime = endTime - startTime
    getExcel01(parseTime,i+1)
}

entity = new addappfunc();

if (candir !=null && candir.exists()&& candir.isDirectory()){
    File[] files = candir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){

            long runStart = System.currentTimeMillis();
            slurper = new JsonSlurper()
            keyconfig=0
            //The number of rows of functions triggered
            keyline=[]
            //0-7:device mode position sunrise sunset sunriseTime sunsetTime app
            eventflag=[]
            notduplicate=[]
            beginnum=0
            installedLinenumber=[]
            time_init_num=[]

            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            filepath = "../sourceFile/testSmartapp/"+filename+".groovy"
            sourcefile = new File(filepath)
            configpath =  "../configFile/allConfig/"+filename+"_config.txt"
            configfile = new File(configpath)
            outputpath = "../outFile/out_can_APP/"+filename+"_out.txt"
            outfile = new File(outputpath)

            if(outfile.exists() && outfile.isFile()){
                outfile.delete()
            }
            outfile.createNewFile()
            configlist = configfile.collect {it}
            //TODO childFlag
            childLine=[]
            childEventlist=[]
            entity.getKey(configlist,slurper,eventflag,beginnum,keyline,notduplicate,installedLinenumber,time_init_num,childEventlist,childLine)
            entity.solveline(sourcefile,keyline)
            //todo childFlag
            entity.initSource(headfile,headfile_sun,sourcefile,outfile,eventflag,keyline,notduplicate,installedLinenumber,time_init_num,i+1,childEventlist,childLine)

            long runEnd = System.currentTimeMillis();
            long runTime = runEnd - runStart

            getExcel02(runTime,i+1)


            println(filename+" done")
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
    String excelFileName = targetFolderPath + "ExcelFullAppTest.xls"
    String sheetName = "ExcelContentSheet"
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook()
    HSSFSheet excelSheet = hssfWorkbook.createSheet(sheetName)
    excelSheet.setDefaultColumnWidth(50)
    HSSFRow hssfRow = null
    HSSFCell hssfCell = null

    FileOutputStream fileOutputStream = new FileOutputStream(excelFileName)
    hssfWorkbook.write(fileOutputStream)
}

def getExcel01(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelFullAppTest.xls"
    HSSFWorkbook work = new HSSFWorkbook(new FileInputStream(excelFileName))
    //HSSFSheet sheet = work.getSheetAt(0)
    HSSFSheet sheet = work.getSheet("ExcelContentSheet")
    //HSSFRow row0 = sheet.getRow(rowNo)
    HSSFRow row0 = sheet.createRow(rowNo)
    HSSFCell cell1 = row0.createCell(1)
    cell1.setCellValue((String)value)

    FileOutputStream out = null
    out = new FileOutputStream(excelFileName)
    work.write(out)
    out.close()
}

def getExcel02(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelFullAppTest.xls"
    HSSFWorkbook work = new HSSFWorkbook(new FileInputStream(excelFileName))
    //HSSFSheet sheet = work.getSheetAt(0)
    HSSFSheet sheet = work.getSheet("ExcelContentSheet")
    HSSFRow row0 = sheet.getRow(rowNo)
    HSSFCell cell1 = row0.createCell(2)
    cell1.setCellValue((String)value)

    FileOutputStream out = null
    out = new FileOutputStream(excelFileName)
    work.write(out)
    out.close()
}
