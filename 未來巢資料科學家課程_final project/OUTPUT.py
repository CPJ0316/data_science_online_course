from operator import index
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

def output(name,Y_pred_model,train_score,test_score,Y_pred_model_test,y_test,predict_data):
  acc_model = round(train_score * 100, 2)
  acc_model_test = round(test_score * 100, 2)
  model_precision = round( precision_score(y_test, Y_pred_model, average="macro") * 100, 2)
  print(y_test, Y_pred_model)
  model_recall = round( recall_score(y_test, Y_pred_model, average="macro") * 100, 2)
  model_fscore = round( f1_score(y_test, Y_pred_model, average="macro") * 100, 2)
  

  models = pd.DataFrame({
    'Train': [name],
    'accuracy': [acc_model],
    'test': [acc_model_test],
    'precision': [model_precision],
    'recall': [model_recall],
    'fscore': [model_fscore],
  })

  models.sort_values(by='accuracy', ascending=False)
  models.to_csv('C:\\Users\\User\\Desktop\\第3組project\\'+name+'綜合表.csv',index=False)
  print(models)

  # 輸出 knn confusion matrix
  model_cm  = confusion_matrix(y_test,Y_pred_model) 
  model_cms = sns.heatmap(model_cm, square=True, annot=True, cbar=False, fmt='g')
  model_cms.set_title('demo_'+name+'_confusion_matrix')
  model_cms.set_xlabel("predicted value")
  model_cms.set_ylabel("true value")
  plt.rcParams['font.sans-serif']=['SimHei']
  plt.rcParams['axes.unicode_minus'] = False
  plt.savefig('C:\\Users\\User\\Desktop\\第3組project\\'+name+'_confusion_matrix.png', dpi=200)
  plt.show()
  plt.close()

  # 進行預測
  predict_result = pd.DataFrame(Y_pred_model_test)
  predict_result.rename( columns={0: 'predict_result'}, inplace=True)
  for i in predict_result.index:
    if predict_result.loc[i,'predict_result']==1:
      predict_result.loc[i,'predict_result']='EXCHANGE'
    elif predict_result.loc[i,'predict_result']==0:
      predict_result.loc[i,'predict_result']='POOL'
    elif predict_result.loc[i,'predict_result']==2:
      predict_result.loc[i,'predict_result']='GAMBLING'

  predict_dataframe = pd.concat([predict_data,predict_result],axis=1)
  #predict_dataframe.sort_values('時間', inplace=True, ascending=True)
  predict_dataframe.to_csv("C:\\Users\\User\\Desktop\\第3組project\\"+name+"_RESULT.csv",index=False)
  #print(predict_dataframe)