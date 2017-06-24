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
	DefaultListModel<String>modelListReady;//��������
	DefaultListModel<String>modelListBackupReady;//�󱸾�������
	DefaultListModel<String>modelListInputWait;//����ȴ�����
	DefaultListModel<String>modelListOutputWait;//����ȴ�����
	DefaultListModel<String>modelListOtherWait;//�����ȴ�����
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
			if(pcb[in].Pname==model.getElementAt(outIndex))//��ý�����
				break;
		return in;
	}//�õ��ڶ��еĵڼ���
	public String JudgeWait(PCB pcb[],@SuppressWarnings("rawtypes") DefaultListModel model,int outIndex)
	{
		
		int index=getPnameindex(model,pcb,outIndex);//�õ��ȴ����и�����������
		String iowname=pcb[index].Pname;
		pcb[index].ins[pcb[index].current]=pcb[index].Pname.charAt(0)+String.valueOf(Integer.parseInt(pcb[index].ins[pcb[index].current].substring(1))-1);
		if(Integer.parseInt(pcb[index].ins[pcb[index].current].substring(1))==0)
		{//˵������ָ����ִ����
			model.removeElementAt(outIndex);//�Ӷ����Ƴ��ý���
			Pro[index].current++;//��һ��ִ����һ��ָ��
			if(Pro[index].current==(Pro[index].insnum-1))
				modelListBackupReady.addElement(iowname);//�����е����һ��ָ���ˣ���ֱ���ƽ��󱸾�������
			else
				modelListReady.addElement(iowname);//��ʱ��ƬΪ0��ֱ�ӽ��ý����Ƶ���������
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
					int index;//�������PCB�����еĵڼ�������
					String ScheName="";
					if(modelListBackupReady.size()!=count)
					{
						if(modelListReady.size()!=0)//�������д��ڽ���
						{
							ScheName=modelListReady.getElementAt(0);
							index=getPnameindex(modelListReady,Pro,0);
							switch(Pro[index].ins[Pro[index].current].charAt(0))
							{
								case 'C':
									Proceding.setText(ScheName);//����Ϊ��ǰ���н���
									modelListReady.removeElementAt(0);//�Ӿ����������Ƴ��ý���																
									Pro[index].ins[Pro[index].current]="C"+(Integer.parseInt(Pro[index].ins[Pro[index].current].substring(1))-1);
									str=("������һ��ʱ��Ƭ�Ľ��� : " +"   "+ScheName +"   "+  Pro[index].ins[Pro[index].current]);
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
									if(Integer.parseInt(Pro[index].ins[Pro[index].current].substring(1))==0)
									{
										Pro[index].current++;//���ý�������CPUʱ����ִ���꣬ת����һ��ָ��
										if(Pro[index].current==(Pro[index].insnum-1))
											modelListBackupReady.addElement(ScheName);//�����е����һ�������ˣ���ֱ���ƽ��󱸾�������							
										else
											modelListReady.addElement(ScheName);//��ʱ��ƬΪ0��ֱ�ӽ��ý����Ƶ���������																				
									}
									else
										modelListReady.addElement(ScheName);//��ʱ��Ƭ��Ϊ0ֱ�ӽ��ý����Ƶ���������										
									//�ж�Input����,Output����,Wait����������û�г����ڵȴ���,����,��ʱ��Ƭ-1
									String IOWName="";
									//input����
									if(modelListInputWait.size()!=0)
									{
										//System.out.println(modelListInputWait.size());
										IOWName=JudgeWait(Pro,modelListInputWait,0);
										str=("��"+ScheName+" ������һ��ʱ��Ƭ��ͬʱ : "+IOWName+"�����һ��ʱ��Ƭ������");
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}									
									//output����
									if(modelListOutputWait.size()!=0)
									{
										//System.out.println(modelListOutputWait.size());
										IOWName=JudgeWait(Pro,modelListOutputWait,0);
										str=("��"+ScheName+" ������һ��ʱ��Ƭ��ͬʱ : "+IOWName+"�����һ��ʱ��Ƭ�����"+"\n");
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}					
									//wait����
									for(int z=0;z<modelListOtherWait.size();z++)
									{
										//System.out.println(modelListOtherWait.size());
										IOWName=JudgeWait(Pro,modelListOtherWait,z);
										str="��"+ScheName+" ������һ��ʱ��Ƭ��ͬʱ : "+IOWName+"�����һ��ʱ��Ƭ�������ȴ�";
										try {
											output.write(str);
											output.newLine();
										} catch (IOException e) {
											e.printStackTrace();
										}
									}									
								break;
								case 'I':
										modelListInputWait.addElement(ScheName);//���ý����Ƶ�����ȴ�����
										modelListReady.removeElementAt(0);//�Ӿ�������ɾ���ý���	
										str=(ScheName+"Ŀǰָ��ΪInput");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								break;
								case 'O':
										modelListOutputWait.addElement(ScheName);//���ý����Ƶ�����ȴ�����
										modelListReady.removeElementAt(0);//�Ӿ�������ɾ���ý���
										str=(ScheName+"Ŀǰָ��ΪOutput");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
								case 'W':
										modelListOtherWait.addElement(ScheName);//���ý����Ƶ������ȴ�����
										modelListReady.removeElementAt(0);//�Ӿ�������ɾ���ý���		
										str=(ScheName+"Ŀǰָ��ΪWait");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
								break;
							}//switch							
						}//if(modelListReady.size()>0)//�������д��ڽ���
						else
						{
							String IOWName="";
							//input����
							if(modelListInputWait.size()!=0)
							{
								//System.out.println(modelListInputWait.size());
								IOWName=JudgeWait(Pro,modelListInputWait,0);
								str=(IOWName+"�����һ��ʱ��Ƭ������ȴ�");
								try {
									output.write(str);
									output.newLine();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
							//output����
							if(modelListOutputWait.size()!=0)
							{
								//System.out.println(modelListOutputWait.size());
								IOWName=JudgeWait(Pro,modelListOutputWait,0);
								str=(IOWName+"�����һ��ʱ��Ƭ������ȴ�");
								try {
									output.write(str);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}						
							//wait����
							for(int z=0;z<modelListOtherWait.size();z++)
							{
								//System.out.println(modelListOtherWait.size());
								IOWName=JudgeWait(Pro,modelListOtherWait,z);
								str=(IOWName+"�����һ��ʱ��Ƭ�������ȴ�");
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
		frame.setTitle("ʱ��Ƭ��ת�����㷨");
		frame.setBounds(100, 100, 833, 518);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnOpenFile = new JButton("���ļ�");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc=new JFileChooser();
				fc.setDialogTitle("���ļ�");   
				JFrame frm=new JFrame("java");    
	            //������ʾ���ļ��ĶԻ���   
	           try{    
	        	   flag=fc.showOpenDialog(frm);    
	           }   
	           catch(HeadlessException head){    
	        	   System.out.println("���ļ�ʧ��");   
	           }
	           if(flag==JFileChooser.APPROVE_OPTION)   
               {   
	        	   //��ø��ļ�   
                   f=fc.getSelectedFile();   
                   try {
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);
						while ((tempS = in.readLine()) != null)
						{
							if(tempS.charAt(0)=='H')
							{
								count++;//�жϽ�����
							}
						}
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);//�ص��ļ�ͷ
						int insNum;
						Pro=new PCB[count];//����PCB����
						for(int i=0;i<count;i++)
						{
							insNum=0;
							Pro[i] = new PCB();
							for(;(tempS = in.readLine()).charAt(0)!='H';)
							{
								if(tempS.charAt(0)=='P')
									Pro[i].Pname=tempS;//������
								insNum++;//ȷ��instruction�ĳ���
							}
							Pro[i].insnum= insNum;
							Pro[i].ins =new String[insNum];//Ϊÿ�����̴���ָ������
							modelListReady.addElement(Pro[i].Pname);//��������ӵ���������
						}
						file_reader = new FileReader(f);
						in = new BufferedReader(file_reader);//�ص��ļ�ͷ
						for(int i=0;i<count;i++)
						{
							for(int j=0;j<Pro[i].insnum;)
							{
								if((tempS = in.readLine()).charAt(0)=='P')
									continue;
								Pro[i].ins[j]=tempS;//Ϊָ��String���鸳ֵ
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
	    	}catch(IOException e11){System.out.println("�ļ���������");}	
          }   
		});
		btnOpenFile.setBounds(14, 29, 113, 27);
		frame.getContentPane().add(btnOpenFile);
		
		JButton btnStartSchedule = new JButton("��ʼ����");
		btnStartSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startTimer();			
			}//actionPerformed
		});//actionListener
		btnStartSchedule.setBounds(176, 29, 113, 27);
		frame.getContentPane().add(btnStartSchedule);
		
		JButton btnStopSchedule = new JButton("��ͣ����");
		btnStopSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				timer.cancel();
			}
		});
		btnStopSchedule.setBounds(344, 29, 113, 27);
		frame.getContentPane().add(btnStopSchedule);
		
		JLabel label = new JLabel("ʱ��Ƭ��С");
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
		
		JLabel label_1 = new JLabel("��ǰ���н���");
		label_1.setBounds(14, 120, 113, 18);
		frame.getContentPane().add(label_1);
		
		Proceding = new JTextField();
		Proceding.setBounds(14, 146, 113, 24);
		frame.getContentPane().add(Proceding);
		Proceding.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("��������");
		lblNewLabel.setBounds(35, 209, 72, 18);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel label_2 = new JLabel("�󱸾�������");
		label_2.setBounds(184, 209, 105, 18);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("����ȴ�����");
		label_3.setBounds(356, 209, 105, 18);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("����ȴ�����");
		label_4.setBounds(532, 209, 105, 18);
		frame.getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("�����ȴ�����");
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
