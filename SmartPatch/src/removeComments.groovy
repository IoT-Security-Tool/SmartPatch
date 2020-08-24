import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

def removeComments(String code)
{
    StringBuilder sb = new StringBuilder()
    int cnt = 0
    for (int i = 0; i < code.length(); i++)
    {
        if(cnt == 0)
        {
            if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
            {
                cnt++
                i++
                continue
            }
        }
        else
        {
            if(i+1 < code.length() && code.charAt(i) == '*' && code.charAt(i+1) == '/')
            {
                cnt--
                i++
                continue
            }
            if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
            {
                cnt++
                i++
                continue
            }
        }
        if(cnt == 0)
        {
            sb.append(code.charAt(i))
        }
    }
    return sb.toString()
}


def removeCommentsWithQuote(String code)
{
    StringBuilder sb = new StringBuilder()
    int cnt = 0
    boolean quoteFlag = false
    for (int i = 0; i < code.length(); i++)
    {
        if(!quoteFlag)
        {
            if(code.charAt(i) == '\"')
            {
                sb.append(code.charAt(i))
                quoteFlag = true
                continue
            }
            else
            {
                if(cnt == 0)
                {
                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
                    {
                        cnt++
                        i++
                        continue
                    }
                }
                else
                {
                    if(i+1 < code.length() && code.charAt(i) == '*' && code.charAt(i+1) == '/')
                    {
                        cnt--
                        i++
                        continue
                    }
                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
                    {
                        cnt++
                        i++
                        continue
                    }
                }
                if(cnt == 0)
                {
                    sb.append(code.charAt(i))
                    continue
                }
            }
        }
        else
        {
            if(code.charAt(i) == '\"' && code.charAt(i-1) != '\\')
            {
                sb.append(code.charAt(i))
                quoteFlag = false
            }
            else
            {
                sb.append(code.charAt(i))
            }
        }

    }
    return sb.toString()
}


def removeCommentsWithQuoteAndDoubleEscape(String code)
{
    StringBuilder sb = new StringBuilder()
    int cnt = 0
    boolean quoteFlag = false
    for (int i = 0; i < code.length(); i++)
    {
        if(!quoteFlag)
        {
            if(code.charAt(i) == '\"')
            {
                sb.append(code.charAt(i))
                quoteFlag = true
                continue
            }
            else if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '/')
            {
                while(code.charAt(i) != '\n')
                {
                    i++
                }
                sb.append(code.charAt(i))
                continue
            }
            else
            {
                if(cnt == 0)
                {
                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
                    {
                        cnt++
                        i++
                        continue
                    }
                }
                else
                {
                    if(i+1 < code.length() && code.charAt(i) == '*' && code.charAt(i+1) == '/')
                    {
                        cnt--
                        i++
                        continue
                    }
                    //发现"/*"嵌套
                    if(i+1 < code.length() && code.charAt(i) == '/' && code.charAt(i+1) == '*')
                    {
                        cnt++
                        i++
                        continue
                    }
                }
                if(cnt == 0)
                {
                    sb.append(code.charAt(i))
                    continue
                }
            }
        }
        else
        {
            if(code.charAt(i) == '\"' && code.charAt(i-1) != '\\')
            {
                sb.append(code.charAt(i))
                quoteFlag = false
            }
            else
            {
                sb.append(code.charAt(i))
            }
        }
    }
    return sb.toString()
}


def ReadFileToString(String FilePath)
{
    FileInputStream fis = null
    BufferedReader br = null
    try
    {
        fis = new FileInputStream(FilePath)
        br = new BufferedReader(new InputStreamReader(fis, "utf-8"))
    }
    catch (IOException e)
    {
        e.printStackTrace()
    }
    StringBuffer sb = new StringBuffer()
    String temp = null
    try
    {
        while((temp = br.readLine()) != null)
        {
            sb.append(temp+'\n')
        }
    } catch (IOException e)
    {
        e.printStackTrace()
    }
    return sb.toString()
}


appdir = new File("../sourceFile/srcAPP")
if (appdir !=null && appdir.exists()&& appdir.isDirectory()){
    File[] files = appdir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){
            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            appfilepath = "../sourceFile/srcAPP/"+filename+".groovy"
            appsourcefile = new File(appfilepath)
            appoutputpath =  "../sourceFile/testcantAPP/"+filename+".groovy"
            appoutputfile = new File(appoutputpath)

            if(appoutputfile.exists() && appoutputfile.isFile()){
                appoutputfile.delete()
            }
            appoutputfile.createNewFile()
            String str = ReadFileToString(appfilepath)
            appoutputfile << removeCommentsWithQuoteAndDoubleEscape(str)
            println(filename+" done")
        }
    } else{
        println("no file exists")
    }
}
else {
    println("there is no such directory")
}

devdir = new File("../sourceFile/srcDevice")
if (devdir !=null && devdir.exists()&& devdir.isDirectory()){
    File[] files = devdir.listFiles()
    if(files !=null && files.length > 0){
        for (int i = 0; i < files.size(); i++){
            String [] sz=files[i].name.split("/")
            filename = sz[sz.length-1].minus(".groovy")

            devfilepath = "../sourceFile/srcDevice/"+filename+".groovy"
            devsourcefile = new File(devfilepath)
            devoutputpath =  "../sourceFile/testcantDevice/"+filename+".groovy"
            devoutputfile = new File(devoutputpath)

            if(devoutputfile.exists() && devoutputfile.isFile()){
                devoutputfile.delete()
            }
            devoutputfile.createNewFile()
            String str = ReadFileToString(devfilepath)
            devoutputfile << removeCommentsWithQuoteAndDoubleEscape(str)
            println(filename+" done")
        }
    } else{
        println("no file exists")    }
}
else {
    println("there is no such directory")
}
