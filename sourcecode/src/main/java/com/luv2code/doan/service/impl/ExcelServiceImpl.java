package com.luv2code.doan.service.impl;

import com.luv2code.doan.entity.Brand;
import com.luv2code.doan.entity.Category;
import com.luv2code.doan.entity.ImageProduct;
import com.luv2code.doan.entity.Product;
import com.luv2code.doan.exceptions.CategoryNotFoundException;
import com.luv2code.doan.exceptions.NotFoundException;
import com.luv2code.doan.exceptions.ProductNotFoundException;
import com.luv2code.doan.repository.CategoryRepository;
import com.luv2code.doan.repository.ProductRepository;
import com.luv2code.doan.service.BrandService;
import com.luv2code.doan.service.CategoryService;
import com.luv2code.doan.service.ExcelService;
import com.luv2code.doan.service.ProductService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {
    private final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    BrandService brandService;

    @Autowired
    CategoryService categoryService;

    public List<Product> convertExcelToListOfProduct(InputStream is) throws NotFoundException {
        List<Product> listProducts = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                if (row.getRowNum() == 0) {
                    // Skip the header row
                    continue;
                }

                Product product = new Product();
                product.setName(row.getCell(0).getStringCellValue());
                product.setPrice((Double) row.getCell(1).getNumericCellValue());


                List<String> listImages = new ArrayList<>();


                product.setIsActive(true);
                product.setDescription(row.getCell(3).getStringCellValue());
                product.setSoldQuantity(0);
                product.setInStock((int) row.getCell(4).getNumericCellValue());
                log.info("sda: {}", row.getCell(0).getStringCellValue());

                log.info("sda: {}", row.getCell(5).getNumericCellValue());
                Brand brand = brandService.getBrandById((int) row.getCell(5).getNumericCellValue());
                Category category = categoryService.getCategoryById((int) row.getCell(6).getNumericCellValue());

                product.setBrands(brand);
                product.setCategories(category);
                product.setRegistrationDate(new Date());

                listProducts.add(product);

                Cell imageCell = row.getCell(2);
                if (imageCell != null && imageCell.getCellType() == CellType.STRING) {
                    String imagesString = imageCell.getStringCellValue();
                    String[] images = imagesString.split(",");
                    listImages = Arrays.asList(images);
                }

                for(String pathImage : listImages) {
                    ImageProduct imageProduct = new ImageProduct();
                    imageProduct.setProduct(product);
                    imageProduct.setPath(pathImage);
//                    imageProductService.addImageProduct(imageProduct);
                }
            }

            workbook.close();


        }
        catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
        return listProducts;
    }
}