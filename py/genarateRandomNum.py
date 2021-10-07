#生成随机数打包成exe
import tkinter as tk
import random
def genarate_random():
    return str(random.randint(1,100))
def control_entry():
    entry_random.delete(0,tk.END)
    entry_random.insert(0,genarate_random())
if __name__ == '__main__':
    root=tk.Tk()
    root.title('生成随机数')
    root.geometry('250x100')
    label_random=tk.Label(root,text='生成1-100以内的随机数')
    entry_random=tk.Entry(root)#输入框
    button_gen=tk.Button(
        root,
        text='生产随机数',
        command=control_entry)
    #三个控件从上往下布局
    label_random.pack()
    entry_random.pack()
    button_gen.pack()
    root.mainloop()