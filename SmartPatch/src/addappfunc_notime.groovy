import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook

def getflag(str){
    def position = 0
    def aftertxt = ''
    for(int i=0; i<str.length(); i++){
        if(str[i] == "{") position = i
    }
    return position
}

def solveline(sourcefile,keyline){
    def lineno = 0
    sourcefile.eachLine { line ->
        lineno++
        for(int i=0; i<keyline.size();i++){
            if( (keyline[i] == lineno) && (!line.contains("{")) ){
                keyline[i] = keyline[i] + 1
            }
        }
    }
}


def isMethod(method,configlist,slurper) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "MethodNode") {
            if(json.name == method){
                keyconfig = i
                return true
            }
        }
    }
    return false
}

def addinslist(flag){
    def installedlist=[]
    installedlist.add("state.URL = \"http://xxx.free.com\"")
    installedlist.add("state.PATH = \"/work\"")
    installedlist.add("state.ID_handler = UUID.randomUUID().toString()")

    def installedlist_sun=[]
    installedlist_sun.add("state.lastEventTime = null")
    installedlist_sun.add("state.timePeriod = 5")

    def installedlist_time=[]
    installedlist_time.add("state.timePeriod = 5")
    installedlist_time.add("state.lastsunrise = null")
    installedlist_time.add("state.lastsunset = null")
    installedlist_time.add("state.LastsunsetTime = null")
    installedlist_time.add("state.LastsunriseTime = null")

    if(flag.contains(0)){
        return installedlist
    }
    if(flag.contains(3) || flag.contains(4)){
        return installedlist_sun
    }
    if(flag.contains(5) || flag.contains(6)){
        return installedlist_time
    }
}

def addinitlist(flag){
    def init_time=[]
    init_time.add("subscribe(location, \"position\", handlerAdded)")
    init_time.add("subscribe(location, \"sunset\", handlerAdded)")
    init_time.add("subscribe(location, \"sunrise\", handlerAdded)")

    if(flag.contains(5) || flag.contains(6)){
        return init_time
    }
}

def initSource(headfile,headfile_sun,sourcefile,outfile,eventflag,keyline,notduplicate,installedLinenumber,time_init_num,rownum){
    def OpPath = "../"
    def sourcelist = sourcefile.readLines()
    def addlist
    def headlist = headfile.readLines()
    def headlist_sun = headfile_sun.readLines()
    def outlist = []
    installflag = false
    initflag = false

    if(eventflag.contains(0)){
        for (int i = 0; i < headlist.size(); i++) {
            outlist<<headlist[i]
        }
    }
    if(eventflag.contains(3) || eventflag.contains(4) || eventflag.contains(5) || eventflag.contains(6)){
        for (int i = 0; i < headlist_sun.size(); i++) {
            outlist<<headlist_sun[i]
        }
    }

    long startTime=System.currentTimeMillis()

    for (int i = 0; i < sourcelist.size(); i++) {
        if( i +1 ==  installedLinenumber ) {
            installflag = true
            for (int j = i; j < sourcelist.size(); j++) {
                if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")) {
                    def str = sourcelist[j].toString()
                    outlist << str.substring(0, str.length() - 1)
                    returnlist = addinslist(eventflag)
                    if (returnlist != null){
                        for (int k = 0; k < returnlist.size(); k++) {
                            outlist<< returnlist[k]
                        }
                    }
                    outlist << "}"
                    sourcelist[j] = ""
                    break
                } else if (sourcelist[j].toString().contains("{")) {
                    outlist << sourcelist[j]

                    returnlist = addinslist(eventflag)
                    if (returnlist != null){
                        for (int k = 0; k < returnlist.size(); k++) {
                            outlist<< returnlist[k]
                        }
                    }
                    sourcelist[j] = ""
                    break
                } else {
                    outlist << sourcelist[j]
                    sourcelist[j] = ""
                }
            }
        }

        if(i +1 == time_init_num[0]){
            initflag = true
            for (int j = i; j < sourcelist.size(); j++) {
                if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")) {
                    def str = sourcelist[j].toString()
                    outlist << str.substring(0, str.length() - 1)
                    returninitlist = addinitlist(eventflag)
                    if (returninitlist != null){
                        for (int k = 0; k < returninitlist.size(); k++) {
                            outlist<< returninitlist[k]
                        }
                    }
                    outlist << "}"
                    sourcelist[j] = ""
                    break
                } else if (sourcelist[j].toString().contains("{")) {
                    outlist << sourcelist[j]
                    returninitlist = addinitlist(eventflag)
                    if (returninitlist != null){
                        for (int k = 0; k < returninitlist.size(); k++) {
                            outlist<< returninitlist[k]
                        }
                    }
                    sourcelist[j] = ""
                    break
                } else {
                    outlist << sourcelist[j]
                    sourcelist[j] = ""
                }
            }
        }

        for(int k=0; k<keyline.size(); k++) {
            if (i == keyline[k] -1 && notduplicate[k] != 0 && eventflag[k] !=7) {
                switch (eventflag[k]) {
                    case 0:
                        addfile = new File(OpPath + "configFile/app_device.groovy")
                        break
                    case 1:
                        addfile = new File(OpPath + "configFile/app_mode.groovy")
                        break
                    case 2:
                        addfile = new File(OpPath + "configFile/app_position.groovy")
                        break
                    case 3:
                        addfile = new File(OpPath + "configFile/app_sunrise.groovy")
                        break
                    case 4:
                        addfile = new File(OpPath + "configFile/app_sunset.groovy")
                        break
                    case 5:
                        addfile = new File(OpPath + "configFile/app_suntime.groovy")
                        break
                    case 6:
                        addfile = new File(OpPath + "configFile/app_suntime.groovy")
                        break
                /*default:
                    addfile = new File(OpPath + "configFile/app_device.groovy")*/
                }
                addlist = addfile.readLines()
                for (int j=i; j < sourcelist.size(); j++){
                    if ((sourcelist[j].toString().contains("{")) && sourcelist[j].toString().contains("}")){
                        def pos = getflag(sourcelist[j])
                        def beforestr = sourcelist[j].toString().substring(0,pos+1)
                        def afterstr = sourcelist[j].toString().substring(pos+1,sourcelist[j].toString().length())

                        outlist << beforestr
                        for (int l = 0; l < addlist.size(); l++) {
                            outlist << addlist[l]
                        }
                        outlist << afterstr
                        sourcelist[j] = ""
                        break
                    }
                    else if(sourcelist[j].toString().contains("{") ){
                        def pos = getflag(sourcelist[j])
                        if( pos == sourcelist[j].length()-1){
                            outlist << sourcelist[j]
                            for (int l = 0; l < addlist.size(); l++) {
                                outlist << addlist[l]
                            }
                            sourcelist[j] = ""
                            break
                        }
                        else{
                            def beforestr = sourcelist[j].toString().substring(0,pos+1)
                            def afterstr = sourcelist[j].toString().substring(pos+1,sourcelist[j].toString().length())
                            outlist << beforestr
                            for (int l = 0; l < addlist.size(); l++) {
                                outlist << addlist[l]
                            }
                            outlist << afterstr
                            sourcelist[j] = ""
                            break
                        }
                    }
                    else {
                        outlist << sourcelist[j]
                        sourcelist[j] = ""
                    }
                }
                /*for (int j = 0; j < addlist.size(); j++) {
                    outlist << addlist[j]
                }*/
            }
        }
        outlist<<sourcelist[i]
    }

    if(eventflag.contains(5) || eventflag.contains(6)){
        timefile = new File(OpPath+"configFile/app_suntime_function.groovy")
        def timelist = timefile.readLines()
        for (int j = 0; j < timelist.size(); j++) {
            outlist<<timelist[j]
        }
    }

    if(!installflag){
        outlist << "def installed() {"
        returnlist = addinslist(eventflag)
        if (returnlist != null){
            for (int k = 0; k < returnlist.size(); k++) {
                outlist<< returnlist[k]
            }
        }
        outlist << "}"
    }
    if(!initflag){
        outlist << "def initialize() {"
        reinitlist = addinitlist(eventflag)
        if (reinitlist != null) {
            for (int k = 0; k < reinitlist.size(); k++) {
                outlist << reinitlist[k]
            }
        }
        outlist << "}"
    }

    long endTime=System.currentTimeMillis()
    println("add time: "+(endTime - startTime)+"ms")
    long runtime = endTime - startTime
    appExcel(runtime,rownum)


    outfile.text=""
    for (int i = 0; i < outlist.size(); i++) {
        outfile<<outlist[i]<<"\n"
    }

}

def getKey(configlist,slurper,eventflag,beginnum,keyline,notduplicate,installedLinenumber,time_init_num) {
    for (int i = 0; i < configlist.size(); i++) {
        def json = slurper.parseText(configlist[i])
        if (json.type == "subscribe") {
            //subscribe app or location
            if(json.arguments.size <= 1) {
                continue
            }
            if(json.arguments.size >= 2 && json.arguments[0] == "location"){
                if(json.arguments[1] == "mode" || json.arguments[1] == "null" ){
                    eventflag[beginnum]= 1
                }
                else if(json.arguments[1] == "position"){
                    eventflag[beginnum]= 2
                }
                else if(json.arguments[1] == "sunrise"){
                    eventflag[beginnum]= 3
                }
                else if(json.arguments[1] == "sunset"){
                    eventflag[beginnum]= 4
                }
                else if(json.arguments[1] == "sunriseTime"){
                    eventflag[beginnum]= 5
                }
                else if(json.arguments[1] == "sunsetTime"){
                    eventflag[beginnum]= 6
                }else {
                    //arguments":["location"," null"," locationHandler"," [filterEvents:false]
                    eventflag[beginnum]= 1
                }
            }else if(json.arguments.size == 2 && json.arguments[0] == "app"){
                eventflag[beginnum]= 7
            }
            else{
                eventflag[beginnum]= 0
            }
            for (int j = 0; j < json.arguments.size(); j++) {
                if(isMethod(json.arguments[j],configlist,slurper)) {
                    def methodjson = slurper.parseText(configlist[keyconfig])
                    keyline[beginnum] = methodjson.linenumber
                }
            }
            beginnum++
        }
        else if(json.name=="installed"){
            installedLinenumber = json.linenumber
        }else if(json.name=="initialize"){
            time_init_num = json.linenumber
        }
    }

    for (int i=0; i<beginnum; i++){
        for (int j=i+1; j<beginnum; j++){
            if((keyline[i] == keyline[j]) && ((eventflag[i]==eventflag[j]) || (eventflag[i]==5 && eventflag[j]==6) ||(eventflag[i]==6 && eventflag[j]==5))){
                notduplicate[j] = 0
            }
        }
    }

}



def appExcel(value,rowNo){
    def targetFolderPath = "../"
    String excelFileName = targetFolderPath + "ExcelApp.xls"
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