package montadormnemonico;

//Conteúdo da lista dos Labels
public class Label {
    private String nomeLabel;
    private String numeroLinha;
        
    public String getNomeLabel(){
        return this.nomeLabel;
    }
    
    public void setNomeLabel(String nomeLabel){
        this.nomeLabel = nomeLabel;
    }
    
    public String getNumeroLinha(){
        return this.numeroLinha;
    }
    
    public void setNumeroLinha(String numeroLinha){
        this.numeroLinha = numeroLinha;
    }
}
