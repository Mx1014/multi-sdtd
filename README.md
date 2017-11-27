输电通道项目二期，所有微服务放入该git目录下。
jdk使用1.8，spring cloud使用1.5.8,配置文件全部采用yml格式

Git 全局设置
```
git config --global user.name "史国栋"
git config --global user.email "gd.shi@rongzhitong.com"
```

创建新版本库
```
git clone http://shiguodong@168.130.7.25/sdtd2/java/sdtd2-task.git
cd sdtd2-task
touch README.md
git add README.md
git commit -m "add README"
git push -u origin master
```

已存在的文件夹

```
cd existing_folder
git init
git remote add origin http://shiguodong@168.130.7.25/sdtd2/java/sdtd2-task.git
git add .
git commit
git push -u origin master
```

已存在的 Git 版本库
```
cd existing_repo
git remote add origin http://shiguodong@168.130.7.25/sdtd2/java/sdtd2-task.git
git push -u origin --all
git push -u origin --tags
```