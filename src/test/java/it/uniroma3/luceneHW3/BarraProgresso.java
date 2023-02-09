package it.uniroma3.luceneHW3;

import javax.swing.JFrame;
import javax.swing.JProgressBar;  

/**
 * @author Angy8489
 */
  public class BarraProgresso extends JFrame{  
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  JProgressBar jb;  

   public BarraProgresso (int min, int max){  
       this.setResizable(true);
       jb=new JProgressBar(min,max);  
       jb.setBounds(0,0,400,60);  //Posizionamento e grandezza della barra
       setSize(400,120);  		   //Grandezza finestra. 
       setLocation(750, 400);
       jb.setValue(0);  		   //Imposta il valore di partenza
       jb.setStringPainted(true);  

       add(jb);   
       setLayout(null);  
    }
   
}