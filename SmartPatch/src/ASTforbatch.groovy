ClassLoader parent = getClass().getClassLoader();
GroovyClassLoader loader = new GroovyClassLoader(parent);

/*The configuration file generation path is in the MyASTTransformation
Default is in allconfig*/

dir = new File("../sourceFile/testDevice")
List filelist=[]
dir.eachFile{filepath->
    if(filepath.isFile())
        if(filepath.name!=".DS_Store")//排除mac系统文件
            filelist.add(filepath.toString())
}
for (int i = 0; i < filelist.size(); i++) {
    Class bClass = loader.parseClass(new File(filelist[i]))
    GroovyObject Object = (GroovyObject)bClass.newInstance()
}




