package com.megapro.invoicesync.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.megapro.invoicesync.dto.ProductMapper;
import com.megapro.invoicesync.dto.request.CreateProductRequestDTO;
import com.megapro.invoicesync.model.Product;

public class ReadProductData {
    static ProductMapper productMapper;
    
    public static List<Product> readExcelFile(String file){
        List<Product> listProduct = new ArrayList<Product>();
        try{
            XSSFWorkbook work = new XSSFWorkbook(new FileInputStream(file));

            XSSFSheet sheet = work.getSheet("Product");
            XSSFRow row = null;

            int i = 0;
            while((row = sheet.getRow(i))!=null) {
                CreateProductRequestDTO productDTO = new CreateProductRequestDTO();
                try{
                    productDTO.setDescription(row.getCell(1).getStringCellValue());
                }
                catch(Exception e){productDTO.setDescription(null);
                }
                try{
                    productDTO.setQuantity(row.getCell(2).getStringCellValue());
                }
                catch(Exception e){ productDTO.setQuantity(null);
                }
                try{
                    productDTO.setPrice(row.getCell(3).getStringCellValue());
                }
                catch(Exception e){productDTO.setPrice(null);
                }
                try{
                    productDTO.setTotalPrice(row.getCell(4).getStringCellValue());
                }
                catch(Exception e){productDTO.setTotalPrice(null);
                }
                Product product = productMapper.createProductRequestToProduct(productDTO);
                listProduct.add(product);
                i++;
            }
            work.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return listProduct;
    }
}
