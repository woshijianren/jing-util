#凡是有疑问的，一定及时提出。

##根据自己对系统的理解，处理好可能出现的不规范数据。 
##报表做完之后，请自己重新过一遍逻辑，然后自己进行简单测试（有时间的话）。
##小细节和错误一定要防止，避免后期频繁修复。
## 测试环境和上线环境出现bug需要对应业务书写人员进行bug修复（一般情况，但可能到时会有其他任务，可能修复会交给他人，请写好注释）

##实体类中：
凡是数字，一律用Long和BigDecimal，不得使用String存储
金额计算一律用public BigDecimal(String xx)的构造方法
Long只用于计算人数等一定不会出现小数的情况。
两者不可混用

字段类型：
取值原始字段为单选，字段必须定义为Map，key一般是获取到的值，value可以为null，主要是防止后期需求修改。


凡是需要展示的字段，必须加上@XSSF注解，且index必须是10递增
header描述其展示字段，可充当备注

类上必须加上@author和@date，以及该类是哪张报表的业务（@describe）。

##业务类中：
每一个字段的取值必须注释其对应中文字段
复杂的报表必须写清楚是如何理解的，并将计算方法列出
简单的报表可以偷懒，但也必须写清楚是哪张报表的业务