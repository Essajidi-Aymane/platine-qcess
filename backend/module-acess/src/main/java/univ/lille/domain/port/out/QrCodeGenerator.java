package univ.lille.domain.port.out;

public interface QrCodeGenerator {

    byte[] generatePng(String content , int width , int height ) ; 
    
}
