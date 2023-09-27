import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import plot_confusion_matrix
from sklearn.metrics import precision_recall_fscore_support
from sklearn.metrics import accuracy_score, f1_score, precision_score, recall_score, classification_report, confusion_matrix
import datetime
import joblib
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn import tree
import pickle
from OUTPUT import output

current_time = datetime.datetime.now()
current = datetime.date.today()
data2=pd.read_excel('C:\\Users\\User\\Desktop\\第3組project\\sheet2_2.xlsx', index_col=None)
print(data2.columns)

#columns=['balance','total_received', 'total_sent','average_in_count','average_out_count','type'] #6的資料夾所執行的程式
#columns=['balance','average_in_count','average_out_count','type'] #4的資料夾所執行的程式
columns=['balance','type'] #balance的資料夾所執行的程式
'''
columns=['n_tx', 'received_tx','sent_tx', 'balance','total_received', 'total_sent', 'lifetime', 'freq_tx',
       'freq_tx_4', 'freq_tx_16', 'freq_tx_52', 'received_max', 'received_min',
       'received_avg', 'received_median', 'received_pr75','received_pr25', 'sent_max', 'sent_min', 'sent_avg', 'sent_median',
       'sent_pr75', 'sent_pr25', 'average_in_count','average_out_count', 'in_days', 'out_days','type']
'''
data=data2[columns]
for i in data.index:
  if data.loc[i,'type']=='EXCHANGE':
    data.loc[i,'type']=int(1)
  elif data.loc[i,'type']=='POOL':
    data.loc[i,'type']=int(0)
  elif data.loc[i,'type']=='GAMBLING':
    data.loc[i,'type']=int(2)
data['type']=data['type'].astype(int)

for i in columns:
  if (data[i].dtypes=='int64'):
    data[i]=data[i].astype(int)
  else:
    data[i]=data[i].astype(float).round(6)

data=data.dropna()


Xdata_original = data[columns].astype(str)
Xdata = Xdata_original.drop(columns='type')
Ydata = data['type']
X_train, X_test, y_train, y_test = train_test_split(Xdata, Ydata.values , test_size=0.3, random_state=10)
predict_data=Xdata.copy()
predict_data.to_csv("C:\\Users\\User\\Desktop\\第3組project\\predict_data.csv",index = False)

predict_data = pd.read_csv('C:\\Users\\User\\Desktop\\第3組project\\predict_data.csv', index_col=None)
for i in predict_data.columns:
  predict_data[i] = predict_data[i].astype(str)

print(predict_data.info())
predit_columns=predict_data.columns
predictX = predict_data[predict_data.columns]

#在4、6兩個資料夾中的數據是沒有run此model的

from sklearn.svm import SVC
svm = SVC(kernel='linear', probability=True)#初始化演算法
svm.fit(X_train, y_train)#把資料丟入演算法
Y_pred_svm = svm.predict(X_test)#儲存預測數值
svm_train_score=svm.score(X_train, y_train)
svm_test_score=svm.score(X_test, y_test)
Y_pred_svm_test=svm.predict(predictX)
pickle.dump(svm, open('C:\\Users\\User\\Desktop\\第3組project\\'+'svm'+'.pkl', 'wb'))

output("svm",Y_pred_svm,svm_train_score,svm_test_score,Y_pred_svm_test,y_test,data2.drop(columns='type'))



from sklearn.tree import DecisionTreeClassifier
for i in range(3,7,1):
  number_str=str(i)
  Dtree=DecisionTreeClassifier(max_depth=i)
  Dtree.fit(X_train,y_train)
  Y_pred_Dtree=Dtree.predict(X_test)
  Dtree_train_score=Dtree.score(X_train, y_train)
  Dtree_test_score=Dtree.score(X_test, y_test)
  Y_pred_Dtree_test=Dtree.predict(predictX)
  pickle.dump(Dtree, open('C:\\Users\\User\\Desktop\\第3組project\\'+'Descion Tree'+number_str+'.pkl', 'wb'))
  output('Descion Tree'+number_str,Y_pred_Dtree,Dtree_train_score,Dtree_test_score,Y_pred_Dtree_test,y_test,data2.drop(columns='type'))
 