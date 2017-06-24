import java.awt.EventQueue;
import javax.swing.DefaultListModel;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;
import java.awt.Color;
import java.awt.SystemColor;

public class OS {
	private int  flag;
	private File f;
	private int count=0;
	private FileReader file_reader;
	private BufferedReader in;
	PCB[] Pro;
	private String tempS;
	private JFrame frame;
	Timer timer;
	BufferedWriter output;
	String str="";
	DefaultListModel<String>modelListReady;//就绪队列
	DefaultListModel<String>modelListBackupReady;//后备就绪队列
	DefaultListModel<String>modelListInputWait;//输入等待队列
	DefaultListModel<String>modelListOutputWait;//输出等待队列
	DefaultListModel<String>modelListOtherWait;//其他等待队列
	private JTextField textFieldTime;
	private JTextField Proceding;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OS window = new OS();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public int getPnameindex(@SuppressWarnings("rawtypes") DefaultListModel model,PCB pcb[],int outIndex)
	{
		int in=0;
		for(in=0;in<count;in++)
			if(pcb[in].Pname==model.getElementAt(outIndex))//获得进程名
				break;
		return in;
	}//得到在队列的第几行
	public String JudgeWait(PCB pcb[],@SuppressWarnings("rawtypes") DefaultListModel model,int outIndex)
	{
		
		int index=getPnameindex(model,pcb,outIndex);//得到等待队列该行名称名字
		String iowname=pcb[index].Pname;
		pcb[index].ins[pcb[index].current]=pcb[index].Pname.charAt(0)+String.valueOf(Integer.parseInt(pcb[index].ins[pcb[index].current].substring(1))-1);
		if(Integer.parseInt(pcb[index].ins[pcb[index].current].substring(1))==0)
		{//说明该条指令已执行完
			model.removeElementAt(outIndex);//从队列移除该进程
			Pro[index].current++;//下一次执行下一条指令
			if(Pro[index].current==(Pro[index].insnum-1))
				modelListBackupReady.addElement(iowname);//若运行到最后一个指令了，则直接移进后备就绪队列
			else
				modelListReady.addElement(iowname);//若时间片为0了直接将该进程移到就绪队列
		}
		return iowname;
	}
	 public  void startTimer(){
	        TimerTask task = new TimerTask() {
	            @Override
	            public void run() {
	            	if(modelListBackupReady.size()==count)
	            	{
	            		try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
	            		timer.cancel();
	            	}
					int index;//储存的是PCB数组中的第几个进程
					String ScheName="";
					if(modelListBackupReady.size()!=count)
					{
						if(modelListReady.size()!=0)//就绪队列存在进程
						{
							ScheName=modelListReady.getElementAt(0);
							index=getPnameindex(modelListReady,Pro,0);
							switch(Pro[index].ins[Pro[index].current].charAt(0))
							{
								case 'C':
									Proceding.setText(ScheName);//设置为当前运行进程
									modelListReady.removeElementAt(0);//从就绪队列中移除该进程																
									Pro[index].ins[Pro[index].current]="C"+(Integer.parseInt(Pro[index].ins[Pro[index].current].substring(1))-1);
									str=("运行了一个时间片的进程 : " +"   "+ScheName +"   "+  Pro[index].ins[Pro[index].current]);
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
									if(Integer.parseInt(Pro[index].ins[Pro[index].current].substring(1))==0)
									{
										Pro[index].current++;//当该进程所需CPU时间已执行完，转到下一条指令
										if(Pro[index].current==(Pro[index].insnum-1))
											modelListBackupReady.addElement(ScheName);//若运行到最后一个程序了，则直接移进后备就绪队列							
										else
											modelListReady.addElement(ScheName);//若时间片为0了直接将该进程移到就绪队列																				
									}
									else
										modelListReady.addElement(ScheName);//若时间片不为0直接将该进程移到就绪队列										
									//判断Input队列,Output队列,Wait队列里面有没有程序在等待着,若有,则时间片-1
									String IOWName="";
									//input队列
									if(modelListInputWait.size()!=0)
									{
										//System.out.println(modelListInputWait.size());
										IOWName=JudgeWait(Pro,modelListInputWait,0);
										str=("在"+ScheName+" 运行了一个时间片的同时 : "+IOWName+"完成了一个时间片的输入");
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}									
									//output队列
									if(modelListOutputWait.size()!=0)
									{
										//System.out.println(modelListOutputWait.size());
										IOWName=JudgeWait(Pro,modelListOutputWait,0);
										str=("在"+ScheName+" 运行了一个时间片的同时 : "+IOWName+"完成了一个时间片的输出"+"\n");
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}					
									//wait队列
									for(int z=0;z<modelListOtherWait.size();z++)
									{
										//System.out.println(modelListOtherWait.size());
										IOWName=JudgeWait(Pro,modelListOtherWait,z);
										str="在"+ScheName+" 运行了一个时间片的同时 : "+IOWName+"完成了一个时间片的其他等待";
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}									
								break;
								case 'I':
										modelListInputWait.addElement(ScheName);//将该进程移到输入等待队列
										modelListReady.removeElementAt(0);//从就绪队列删除该进程	
										str=(ScheName+"目前指令为Input");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								break;
								case 'O':
										modelListOutputWait.addElement(ScheName);//将该进程移到输出等待队列
										modelListReady.removeElementAt(0);//从就绪队列删除该进程
										str=(ScheName+"目前指令为Output");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
								case 'W':
										modelListOtherWait.addElement(ScheName);//将该进程移到其他等待队列
										modelListReady.removeElementAt(0);//从就绪队列删除该进程		
										str=(ScheName+"目前指令为Wait");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							}//switch							
						}//if(modelListReady.size()>0)//就绪队列存在进程
						else
						{
							String IOWName="";
							//input队列
							if(modelListInputWait.size()!=0)
							{
								//System.out.println(modelListInputWait.size());
								IOWName=JudgeWait(Pro,modelListInputWait,0);
								str=(IOWName+"完成了一个时间片的输入等待");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
							//output队列
							if(modelListOutputWait.size()!=0)
							{
								//System.out.println(modelListOutputWait.size());
								IOWName=JudgeWait(Pro,modelListOutputWait,0);
								str=(IOWName+"完成了一个时间片的输出等待");
								try {
									output.write(str);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}						
							//wait队列
							for(int z=0;z<modelListOtherWait.size();z++)
							{
								//System.out.println(modelListOtherWait.size());
								IOWName=JudgeWait(Pro,modelListOtherWait,z);
								str=(IOWName+"完成了一个时间片的其他等待");
								try {
									output.write(str);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}//else
					}//while(!stop)
	            }
	        };
	        timer = new Timer();
	        timer.schedule(task, 0,Integer.parseInt(textFieldTime.getText()));
	    }
	
	public OS() {
		initialize();
	}
	private void initialize() {
		frame = new JFrame();
		frame.setForeground(SystemColor.inactiveCaption);
		frame.setTitle("时间片轮转调度算法");
		frame.setBounds(100, 100, 833, 518);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnOpenFile = new JButton("打开文件");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setDialogTitle("打开文件");   
				JFrame frm=new JFrame("java");    
	            //这里显示打开文件的对话框   
	           try{    
	        	   flag=fc.showOpenDialog(frm);    
	           }   
	           catch(HeadlessException head){    
	        	   System.out.println("打开文件失败");   
	           }
	           if(flag==JFileChooser.APPROVE_OPTION)   
               {   
	        	   //获得该文件   
                   f=fc.getSelectedFile();   
                   try {
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);
						while ((tempS = in.readLine()) != null)
						{
							if(tempS.charAt(0)=='H')
							{
								count++;//判断进程数
							}
						}
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);//回到文件头
						int insNum;
						Pro=new PCB[count];//创建PCB数组
						for(int i=0;i<count;i++)
						{
							insNum=0;
							Pro[i] = new PCB();
							for(;(tempS = in.readLine()).charAt(0)!='H';)
							{
								if(tempS.charAt(0)=='P')
									Pro[i].Pname=tempS;//进程名
								insNum++;//确定instruction的长度
							}
							Pro[i].insnum= insNum;
							Pro[i].ins =new String[insNum];//为每个进程创建指令数组
							modelListReady.addElement(Pro[i].Pname);//将进程添加到就绪队列
						}
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);//回到文件头
						for(int i=0;i<count;i++)
						{
							for(int j=0;j<Pro[i].insnum;)
							{
								if((tempS = in.readLine()).charAt(0)=='P')
									continue;
								Pro[i].ins[j]=tempS;//为指令String数组赋值
								j++;
							}
						}		
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}                      
                }
	       	try{
	    		output = new BufferedWriter(new FileWriter("C:\\Users\\LVV\\Desktop\\OS.txt"));
	    	}catch(IOException e11){System.out.println("文件操作出错");}	
          }   
		});
		btnOpenFile.setBounds(14, 29, 113, 27);
		frame.getContentPane().add(btnOpenFile);
		
		JButton btnStartSchedule = new JButton("开始调度");
		btnStartSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTimer();			
			}//actionPerformed
		});//actionListener
		btnStartSchedule.setBounds(176, 29, 113, 27);
		frame.getContentPane().add(btnStartSchedule);
		
		JButton btnStopSchedule = new JButton("暂停调度");
		btnStopSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				timer.cancel();
			}
		});
		btnStopSchedule.setBounds(344, 29, 113, 27);
		frame.getContentPane().add(btnStopSchedule);
		
		JLabel label = new JLabel("时间片大小");
		label.setBounds(526, 29, 81, 27);
		frame.getContentPane().add(label);
		
		textFieldTime = new JTextField();
		textFieldTime.setText("500");
		textFieldTime.setBounds(618, 30, 86, 24);
		frame.getContentPane().add(textFieldTime);
		textFieldTime.setColumns(10);
		
		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(714, 33, 29, 18);
		frame.getContentPane().add(lblMs);
		
		JLabel label_1 = new JLabel("当前运行进程");
		label_1.setBounds(14, 120, 113, 18);
		frame.getContentPane().add(label_1);
		
		Proceding = new JTextField();
		Proceding.setBounds(14, 146, 113, 24);
		frame.getContentPane().add(Proceding);
		Proceding.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("就绪队列");
		lblNewLabel.setBounds(35, 209, 72, 18);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel label_2 = new JLabel("后备就绪队列");
		label_2.setBounds(184, 209, 105, 18);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("输入等待队列");
		label_3.setBounds(356, 209, 105, 18);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("输出等待队列");
		label_4.setBounds(532, 209, 105, 18);
		frame.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("其他等待队列");
		label_5.setBounds(698, 209, 105, 18);
		frame.getContentPane().add(label_5);
		
		modelListReady = new DefaultListModel<String>();
		JList<String>listReady = new JList<>(modelListReady);
		listReady.setBounds(14, 251, 105, 196);
		frame.getContentPane().add(listReady);	
		
		modelListBackupReady = new DefaultListModel<String>();
		JList<String>listBackup = new JList<>(modelListBackupReady);
		listBackup.setBounds(176, 251, 105, 196);
		frame.getContentPane().add(listBackup);
		
		modelListInputWait = new DefaultListModel<String>();
		JList<String>listInputWait = new JList<>(modelListInputWait);
		listInputWait.setBounds(352, 251, 105, 196);
		frame.getContentPane().add(listInputWait);
		
		modelListOutputWait = new DefaultListModel<String>();
		JList<String>listOutputWait = new JList<>(modelListOutputWait);
		listOutputWait.setBounds(526, 251, 105, 196);
		frame.getContentPane().add(listOutputWait);
		
		modelListOtherWait = new DefaultListModel<String>();
		JList<String>listOtherWait = new JList<>(modelListOtherWait);
		listOtherWait.setBounds(698, 251, 105, 196);
		frame.getContentPane().add(listOtherWait);
	}
}
