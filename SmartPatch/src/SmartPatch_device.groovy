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
    long startTime = System.currentTimeMillis();
    Class bClass = loader.parseClass(new File(filelist[i]))
    GroovyObject Object = (GroovyObject)bClass.newInstance()
    long endTime = System.currentTimeMillis();
    long parseTime = endTime - startTime
    getDevExcel01(parseTime,i+1)
}

entire = new adddevfunc();

if (candir !=null && candir.exists()&& candir.isDirectory()){
    File[] files = candir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){

            long runStart = System.currentTimeMillis();

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

            long runEnd = System.currentTimeMillis();
            long runTime = runEnd - runStart
            getDevExcel02(runTime,i+1)

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

/*def createExcel(){

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
}*/
def createExcel(){

    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelFullDevTest.xls"
    String sheetName = "ExcelContentSheet"
    HSSFWorkbook hssfWorkbook = new HSSFWorkbook()
    HSSFSheet excelSheet = hssfWorkbook.createSheet(sheetName)
    excelSheet.setDefaultColumnWidth(50)
    HSSFRow hssfRow = null
    HSSFCell hssfCell = null

    FileOutputStream fileOutputStream = new FileOutputStream(excelFileName)
    hssfWorkbook.write(fileOutputStream)
}

def getDevExcel01(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelFullDevTest.xls"
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

def getDevExcel02(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelFullDevTest.xls"
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
