package montadormnemonico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;

public class MontadorMnemonico {

    public static LinkedList <Label> labels = new LinkedList <>();
    
    public static void main(String[] args) {
        Montador montador = new Montador(); // Instanciar o Montador
        HashMap <Integer, String> comandos = new HashMap <>();
        System.out.println("----Montador Mnemômico----");
        montador.mostrarRegistradores();
        Label label;
        
        try { //Abrir arquivo com os comandos
            BufferedReader br = new BufferedReader(new FileReader("Entradas"));
            FileWriter fw = new FileWriter("Saidas");
            PrintWriter pw = new PrintWriter(fw);
            
            //Procurar por Labels
            while(br.ready()){
                String linha = br.readLine();
                if(linha.endsWith(":")){
                    String nomeLabel = linha.substring(0, linha.length() - 1);
                    
                    //Ler a próxima linha para armazenar o valor na lista dos Labels
                    linha = br.readLine();
                    
                    //Achar e copiar o número da linha
                    int posEspaco = linha.indexOf(" ");
                    String numLinhaLabel = linha.substring(0, posEspaco);
                    
                    //Instanciar o label, armazenar valores no objeto e depositar na lista
                    label = new Label();
                    label.setNomeLabel(nomeLabel);
                    label.setNumeroLinha(numLinhaLabel);
                    labels.add(label);
                }
            }
            
            //Fechar o arquivo e abrir para leitura novamente
            br.close();
            br = new BufferedReader(new FileReader("Entradas"));
            
            while(br.ready()){
                //Ler as linha que não são labels e transformar em maiúscula
                String linha = br.readLine();
                if(!linha.endsWith(":")){
                    linha = linha.toUpperCase();
                
                    //Dividir o número da linha do comando
                    int ultimaPos = linha.indexOf(" ");                
                    String numeroLinha = linha.substring(0, ultimaPos);
                    int numeroLinhaTemporario = Integer.parseInt(numeroLinha);
                
                    linha = linha.substring(2, linha.length());
                    
                    //Colocar na lista/mapa
                    comandos.put(numeroLinhaTemporario, linha); 
                }                
            }
            
            br.close();
            
            int numLinha = 1;
            
            //Tratar os comandos
            do{
                System.out.println(numLinha + " " +comandos.get(numLinha));
                montador.setComando(comandos.get(numLinha));
                montador.setNumeroLinha(numLinha+"");
                pw.println(montador.getNumeroLinha() + " " + montador.getPalavra());
                
                //Identificar se o comando é JUMP
                if(comandos.get(numLinha).startsWith("JMP")){
                    //Dividir o comando do primeiro argumento. EX: JUMP 00001000
                    int posEspaco = comandos.get(numLinha).indexOf(" ");
                    String arg1 = comandos.get(numLinha).substring(posEspaco + 1);
                    numLinha = Integer.parseInt(arg1, 2);                    
                }
                
                //Identificar se o comando é CALL
                else if(comandos.get(numLinha).startsWith("CALL")){
                    //Identificar o Label
                    int posEspaco = comandos.get(numLinha).indexOf(" ");
                    String comandoLabel = comandos.get(numLinha).substring(posEspaco + 1);
                    
                    //Procurar na lista dos Labels
                    int numeroLista = 0;
                    while(numeroLista < labels.size()){
                        label = labels.get(numeroLista);
                        if(comandoLabel.equals(label.getNomeLabel())){
                            numLinha = Integer.parseInt(label.getNumeroLinha());
                            break;
                        }
                    }
                }
                
                //Identificar se o comando é HALT (de parada).
                else if(comandos.get(numLinha).equals("HALT")){
                    break;
                }         
                
                else{
                    numLinha++;
                }              
            }while(true);                                
            
            pw.close();
            fw.close();
        } 
        catch(IOException ex){
            ex.getMessage();
        }
    }
}
