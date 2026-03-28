package com.zenithflow.quant.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zenithflow.commons.dynamic.datasource.annotation.DataSource;
import com.zenithflow.quant.mapper.BarMapper;
import com.zenithflow.quant.model.Bar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@DataSource("quant")
public class DataService extends ServiceImpl<BarMapper, Bar> {

    /**
     * 将 CSV 数据导入数据库 (ETL)
     */
    @Transactional
    public void importCsvToDb(String csvPath, String symbol) {
        File file = new File(csvPath);
        if (!file.exists()) {
            log.error("CSV file not found: {}", csvPath);
            return;
        }

        List<Bar> buffer = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { isHeader = false; continue; }
                
                String[] values = line.split(",");
                LocalDate date = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                
                // 幂等性检查：避免重复插入
                // 使用 MyBatis Plus 的 LambdaQueryWrapper
                long count = this.count(new LambdaQueryWrapper<Bar>()
                        .eq(Bar::getSymbol, symbol)
                        .eq(Bar::getDate, date));
                        
                if (count > 0) {
                    continue;
                }
                
                Bar bar = Bar.builder()
                        .symbol(symbol)
                        .date(date)
                        .open(new BigDecimal(values[1]))
                        .close(new BigDecimal(values[2]))
                        .high(new BigDecimal(values[3]))
                        .low(new BigDecimal(values[4]))
                        .volume(Long.parseLong(values[5]))
                        .amount(new BigDecimal(values[6]))
                        .build();
                buffer.add(bar);
            }
            
            if (!buffer.isEmpty()) {
                // 使用 ServiceImpl 提供的批量保存方法
                this.saveBatch(buffer);
                log.info("Imported {} bars for {}", buffer.size(), symbol);
            } else {
                log.info("No new data to import for {}", symbol);
            }
            
        } catch (Exception e) {
            log.error("Error importing CSV", e);
        }
    }
    
    public List<Bar> loadFromDb(String symbol) {
        // 按日期升序查询
        return this.list(new LambdaQueryWrapper<Bar>()
                .eq(Bar::getSymbol, symbol)
                .orderByAsc(Bar::getDate));
    }
}
