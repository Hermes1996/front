package com.bessky.warehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务层启动类
 *
 * @author liunancun
 * @date 2020/2/24
 */
@SpringBootApplication
@MapperScan("com.bessky.warehouse.**.mapper")
public class BesskyWarehouseServiceApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(BesskyWarehouseServiceApplication.class, args);
    }
}
