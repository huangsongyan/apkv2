### apkv2详解

#### End of central directory record (EOCD)
Offset  |  Bytes |  Description[23]
:------ |  :---: |  -------------------:
 0      |    4   |  End of central directory signature = 0x06054b50
 4      |    2   |  Number of this disk
 6      |    2   |  Disk where central directory starts
 8      |    2   |  Number of central directory records on this disk
 10     |    2   |  Total number of central directory records
 12     |    4   |  Size of central directory (bytes)
 16     |    4   |  Offset of start of central directory, relative to start of archive
 20     |    2   |  Comment length (n)
 22     |    n   |  Comment
 
       

`首先找到中央目录结尾区的0x06054b50，接着可以找到中央目录的偏移，再根据签名区的规则找到签名信息`

参考 https://source.android.com/security/apksigning/v2

https://www.jianshu.com/p/8d4396ce231f