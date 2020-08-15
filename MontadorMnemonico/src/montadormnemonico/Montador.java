package montadormnemonico;

//Esta classe define como é o modelo orientado a objetos do Montador baixo nível 

import static montadormnemonico.MontadorMnemonico.labels;

 
public class Montador {
    private String comando;
    private int[] registradores = new int[10];
    private String palavra = "";
    private String numeroLinha = "";
    
    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }
    
    //Pegar o número da linha e retornar pra classe principal
    //Transformar string em int pra poder transformar depois int em binário
    public String getNumeroLinha(){
        int numeroLinhaTemporario = Integer.parseInt(this.numeroLinha);
        this.numeroLinha = Integer.toBinaryString(numeroLinhaTemporario);
        this.numeroLinha = completarEsquerda(numeroLinha, 8);
        return numeroLinha;
    }
    
    public void setNumeroLinha(String numeroLinha){
        this.numeroLinha = numeroLinha;
    }
    
    //Inicializar os valores dos registradores
    public void inicializar(){
        for (int i = 0; i < 10; i++)
            registradores[i] = 0;       
    }
    
    //Método construtor da classe, chamando a inicialização
    public Montador(){
        this.inicializar();
    }
    
    //Mostrar os conteúdos dos registradores
    public void mostrarRegistradores(){
        for (int i = 0; i < 10; i++)
            System.out.println("R" + i + ": " + this.registradores[i]);               
    }
    
    /*Não é necessário
    public String getComando(){
        return this.comando;
    }*/
    
    public void setComando(String comando){
        this.comando = comando;
        tratarComando(comando);
    }
    
    /*Não é necessário
    public int[] getRegistradores(){
        return this.registradores;
    }
    
    public void setRegistradores(int[] registradores){
        this.registradores = registradores;
    }*/
    
    //Completar à esquerda com zeros
    public static String completarEsquerda(String parametro, int tam){
        int tamanho = tam - parametro.length();
        String comandoTemp = "";
        for (int i = 0; i < tamanho; i++) {
            comandoTemp += "0";            
        }
        parametro = comandoTemp + parametro;
        return parametro;
    }
    
    
    public void mostrarPalavra(int reconhecerComando, int enderecoArg1, String arg2){
        String comando = Integer.toBinaryString(reconhecerComando);
        comando = completarEsquerda(comando, 8);
        String arg1 = Integer.toBinaryString(enderecoArg1);
        arg1 = completarEsquerda(arg1, 8);
        int enderecoArg2, flag = 0;
        String auxiliarDecimal, segundaPalavra, palavra;
        
        if(arg2.startsWith("R")){
            enderecoArg2 = Integer.valueOf(arg2.substring(1))*24;
            arg2 = Integer.toBinaryString(enderecoArg2);
        } 
        
        else if(arg2.startsWith("@")){
            enderecoArg2 = Integer.valueOf(arg2.substring(2));
            int enderecoRegistradores[] = {0, 24, 48, 72, 96, 120, 144, 168, 192, 216};
            arg2 = Integer.toBinaryString(enderecoRegistradores[enderecoArg2]);
        }
        
        else if(arg2.startsWith("#")){
            auxiliarDecimal = arg2.substring(1, arg2.length());
            int decimal = Integer.valueOf(auxiliarDecimal);
            arg2 = Integer.toBinaryString(decimal);
            if(decimal > 255){
                flag = 1;
                palavra = comando + arg1 + 11111111;
                segundaPalavra = completarEsquerda(arg2, 24);
                this.setPalavra(palavra + segundaPalavra);
            }
        }
        
        else if(arg2.startsWith("0") || arg2.startsWith("1")){
            if(arg2.length() > 8){
                flag = 1;
                palavra = comando + arg1 + 11111111;
                segundaPalavra = completarEsquerda(arg2, 24);
                this.setPalavra(palavra +"\n\t "+ segundaPalavra);
            }
        }
        
        if(flag == 0){
            if(!arg2.equals("")){
                arg2 = completarEsquerda(arg2, 8);
                palavra = comando + arg1 + arg2;
            }
            else{
               palavra = comando + arg1 + 11111111;
            }
            this.setPalavra(palavra);
        }        
    }
    
    private void tratarComando(String comando){
        String elementos[] = comando.split(" ");
        String operacao, arg1 = "", arg2 = "", auxiliarDecimal;
        operacao = elementos[0];
        if(elementos.length > 1)
            arg1 = elementos[1];
        if(elementos.length > 2)
            arg2 = elementos[2];
        
        int valorArg1 = 0, enderecoArg1;       
        int enderecoArg2, reconhecerComando = 0;
        int valorEstoura = 8388608;
        int valor25Bits = 16777216;
        int decimal, binarioDecimal;
        
        if(operacao.equals("ADD")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            if(arg2.startsWith("R")){
                enderecoArg2 = Integer.valueOf(arg2.substring(1));
                
                this.registradores[valorArg1] += registradores[enderecoArg2];      
                reconhecerComando = 0;
            }
            
            else if(arg2.startsWith("@")){
                //Retirar o @ e R do segundo argumento
                enderecoArg2 = Integer.valueOf(arg2.substring(2));
                
                this.registradores[valorArg1] += (enderecoArg2 * 24);
                reconhecerComando = 1;
            }
            
            else if(arg2.startsWith("#")){
                //Retirar o # do segundo argumento
                auxiliarDecimal = arg2.substring(1, arg2.length());
                //Converter string auxiliar do decimal em inteiro
                decimal = Integer.valueOf(auxiliarDecimal);
                
                this.registradores[valorArg1] += decimal;
                if (decimal > 255) {
                    reconhecerComando = 3;
                } 
                else {
                    reconhecerComando = 2;
                }
            }
            
            //Se o segundo parâmetro começar com 0 ou 1. Ex: ADD R3 101
            else if(arg2.startsWith("0") || arg2.startsWith("1")){
                binarioDecimal = Integer.parseInt(arg2, 2);
                
                this.registradores[valorArg1] += binarioDecimal;  
                if (arg2.length() > 8) {
                    reconhecerComando = 5;
                } 
                else {
                    reconhecerComando = 4;
                }
            }
        }
        
        else if(operacao.equals("SUB")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            if(arg2.startsWith("R")){
                enderecoArg2 = Integer.valueOf(arg2.substring(1));
                
                this.registradores[valorArg1] -= registradores[enderecoArg2]; 
                reconhecerComando = 6;
            }
            
            else if(arg2.startsWith("@")){
                enderecoArg2 = Integer.valueOf(arg2.substring(2));
                
                this.registradores[valorArg1] -= (enderecoArg2 * 24);
                reconhecerComando = 7;
            }
            
            else if(arg2.startsWith("#")){
                //Retirar o # do segundo argumento
                auxiliarDecimal = arg2.substring(1, arg2.length());
                //Converter string do decimal em inteiro
                decimal = Integer.valueOf(auxiliarDecimal);
                
                this.registradores[valorArg1] -= decimal;                
                if (decimal > 255) {
                    reconhecerComando = 9;
                } 
                else {
                    reconhecerComando = 8;
                }                
            }
            
            //Se o segundo parâmetro começar com 0 ou 1. Ex: ADD R3 101
            else if(arg2.startsWith("0") || arg2.startsWith("1")){
                binarioDecimal = Integer.parseInt(arg2, 2);
                
                this.registradores[valorArg1] -= binarioDecimal;  
                if (arg2.length() > 8) {
                    reconhecerComando = 11;
                } 
                else {
                    reconhecerComando = 10;
                }
            }
        }
        
        else if(operacao.equals("MULT")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            if(arg2.startsWith("R")){
                enderecoArg2 = Integer.valueOf(arg2.substring(1));
                
                this.registradores[valorArg1] *= registradores[enderecoArg2];
                reconhecerComando = 12;
            }
            
            else if(arg2.startsWith("@")){
                enderecoArg2 = Integer.valueOf(arg2.substring(2));
                
                this.registradores[valorArg1] *= (enderecoArg2 * 24);
                reconhecerComando = 13;
            }
            
            else if(arg2.startsWith("#")){
                //Retirar o # do segundo argumento
                auxiliarDecimal = arg2.substring(1, arg2.length());
                //Converter string do decimal em inteiro
                decimal = Integer.valueOf(auxiliarDecimal);
                
                this.registradores[valorArg1] *= decimal;                
                if (decimal > 255) {
                    reconhecerComando = 15;
                } 
                else {
                    reconhecerComando = 14;
                }                
            }
            
            //Se o segundo parâmetro começar com 0 ou 1. Ex: ADD R3 101
            else if(arg2.startsWith("0") || arg2.startsWith("1")){                
                binarioDecimal = Integer.parseInt(arg2, 2);
                
                this.registradores[valorArg1] *= binarioDecimal;  
                if (arg2.length() > 8) {
                    reconhecerComando = 17;
                } 
                else {
                    reconhecerComando = 16;
                }
            }
        }
        
        else if(operacao.equals("DIV")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            if(arg2.startsWith("R")){
                enderecoArg2 = Integer.valueOf(arg2.substring(1));
                
                this.registradores[valorArg1] /= registradores[enderecoArg2];
                reconhecerComando = 18;
            }
            
            else if(arg2.startsWith("@")){
                enderecoArg2 = Integer.valueOf(arg2.substring(2));
                
                this.registradores[valorArg1] /= (enderecoArg2 * 24);
                reconhecerComando = 19;
            }
            
            else if(arg2.startsWith("#")){
                //Retirar o # do segundo argumento
                auxiliarDecimal = arg2.substring(1, arg2.length());
                //Converter string do decimal em inteiro
                decimal = Integer.valueOf(auxiliarDecimal);
                
                this.registradores[valorArg1] /= decimal;                
                if (decimal > 255) {
                    reconhecerComando = 21;
                } 
                else {
                    reconhecerComando = 20;
                }                
            }
            
            //Se o segundo parâmetro começar com 0 ou 1. Ex: ADD R3 101
            else if(arg2.startsWith("0") || arg2.startsWith("1")){                
                binarioDecimal = Integer.parseInt(arg2, 2);
                
                this.registradores[valorArg1] /= binarioDecimal;  
                if (arg2.length() > 8) {
                    reconhecerComando = 23;
                } 
                else {
                    reconhecerComando = 22;
                }
            }
        }
        
        else if(operacao.equals("DESQ")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            //Pegar o valor que está dentro do registrador
            int valorRegistrador = this.registradores[valorArg1];
            
            /*valorEstoura = 2^23 = 8388608 (valor máximo), caso na 25ª casa não seja 1
            Nenhum bit será perdido*/
            if(valorRegistrador * 2 < valorEstoura){
                this.registradores[valorArg1] = valorRegistrador * 2;
            }
            
            else{
                this.registradores[valorArg1] = (valorRegistrador *2) - valor25Bits;
            }
            reconhecerComando = 24;
        }
        
        else if(operacao.equals("DDIR")){
            valorArg1 = Integer.valueOf(arg1.substring(1));
            int valorRegistrador = this.registradores[valorArg1] / 2;
            this.registradores[valorArg1] = valorRegistrador;
            reconhecerComando = 25;
        }
        enderecoArg1 = valorArg1 * 24;
        
        if(operacao.equals("HALT")){
            //Pegar a posição 10 do vetor, que é equivalente à 255 em binário
            enderecoArg1 = 255;
            reconhecerComando = 255;
        }
        
        else if(operacao.equals("JMP")){
            enderecoArg1 = Integer.parseInt(arg1, 2);
            reconhecerComando = 26;
        }
        
        else if(operacao.equals("CALL")){
            //Procurar o Label na lista
            int numeroLista = 0;
            
            while(numeroLista < labels.size()){
                Label label = labels.get(numeroLista);
                if(arg1.equals(label.getNomeLabel())){
                    enderecoArg1 = Integer.parseInt(label.getNumeroLinha());
                    break;
                }                
            }
            
            reconhecerComando = 27;
        }
        
        this.mostrarRegistradores();
        mostrarPalavra(reconhecerComando, enderecoArg1, arg2);
    }    
}