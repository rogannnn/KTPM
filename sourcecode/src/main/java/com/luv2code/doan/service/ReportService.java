package com.luv2code.doan.service;

import com.luv2code.doan.bean.ReportItem;
import com.luv2code.doan.bean.SaleHistoryItem;
import com.luv2code.doan.bean.SoldByCategoryItem;
import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.exceptions.OrderStatusNotFoundException;
import com.luv2code.doan.repository.OrderStatusRepository;
import com.luv2code.doan.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;


public interface ReportService {


    public long getTotalReviewInMonth();

    public long getTotalUserInMonth();

    public long getTotalOrderInMonth();


    public double getTotalRevenueInMonth();


    public List<SaleHistoryItem> getSaleHistoryByYear();


    public List<SaleHistoryItem> getBuyHistoryOfUserByYear(Integer userId);

    public List<SaleHistoryItem> getBuyHistoryByMonthOfUser(Integer userId);

    public List<SaleHistoryItem> getBuyHistoryByWeek(Integer userId);

    public List<SaleHistoryItem> getSaleHistoryByMonth();

    public List<SaleHistoryItem> getSaleHistoryByWeek();

    public List<ReportItem> reportOrder(Integer type) throws OrderStatusNotFoundException;

    public long countOrderByWeekAndStatusId(Integer statusId) throws OrderStatusNotFoundException;

    public long countOrderByMonthAndStatusId(Integer statusId) throws OrderStatusNotFoundException;

    public long countOrderByYearAndStatusId(Integer statusId) throws OrderStatusNotFoundException;

    public List<ReportItem> getOverviewUserByWeek();

    public List<ReportItem> getOverviewUserByMonth();


    public List<ReportItem> getOverviewUserByYear();


    public List<ReportItem> getOverviewReviewByWeek();

    public List<ReportItem> getOverviewReviewByMonth();


    public List<ReportItem> getOverviewReviewByYear();


    public List<SoldByCategoryItem> getSoldByCategory();


    public Date subDays(Date date, int days);

}
