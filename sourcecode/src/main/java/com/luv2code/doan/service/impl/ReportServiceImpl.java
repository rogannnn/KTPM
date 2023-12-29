package com.luv2code.doan.service.impl;

import com.luv2code.doan.bean.ReportItem;
import com.luv2code.doan.bean.SaleHistoryItem;
import com.luv2code.doan.bean.SoldByCategoryItem;
import com.luv2code.doan.entity.OrderStatus;
import com.luv2code.doan.exceptions.OrderStatusNotFoundException;
import com.luv2code.doan.repository.ReportRepository;
import com.luv2code.doan.service.OrderStatusService;
import com.luv2code.doan.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;


    @Autowired
    private OrderStatusService orderStatusService;

    public long getTotalReviewInMonth() {
        return reportRepository.getTotalReviewInMonth();
    }

    public long getTotalUserInMonth() {
        return reportRepository.getTotalUserInMonth();
    }

    public long getTotalOrderInMonth() {
        return reportRepository.getTotalOrderInMonth();
    }


    public double getTotalRevenueInMonth() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        return reportRepository.getTotalRevenueInMonth(month + 1, year);
    }


    public List<SaleHistoryItem> getSaleHistoryByYear() {

        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH) + 1;

        YearMonth current = YearMonth.of(currYear, currMonth);


        List<SaleHistoryItem> list = new ArrayList<>();


        for (int i = 11; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();

            YearMonth yearMonth = current.minusMonths(i);

            int month = yearMonth.getMonth().getValue();
            int year = yearMonth.getYear();
            item.setName(capitalizeFirstLetter(yearMonth.getMonth().toString().toLowerCase(Locale.ROOT)) + ", " + year);
            item.setValue(reportRepository.getTotalRevenueInMonth(month, year));

            list.add(item);
        }

        return list;
    }


    public List<SaleHistoryItem> getBuyHistoryOfUserByYear(Integer userId) {

        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH) + 1;

        YearMonth current = YearMonth.of(currYear, currMonth);


        List<SaleHistoryItem> list = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();

            YearMonth yearMonth = current.minusMonths(i);

            int month = yearMonth.getMonth().getValue();
            int year = yearMonth.getYear();
            item.setName(capitalizeFirstLetter(yearMonth.getMonth().toString().toLowerCase(Locale.ROOT)) + ", " + year);
            item.setValue(reportRepository.getTotalReviewInMonthOfUser(month, year, userId));

            list.add(item);
        }

        return list;
    }

    public List<SaleHistoryItem> getBuyHistoryByMonthOfUser(Integer userId) {
        List<SaleHistoryItem> list = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.totalBuyHistoryByDateOfUser(d, userId));

            list.add(item);
        }

        return list;
    }

    public List<SaleHistoryItem> getBuyHistoryByWeek(Integer userId) {
        List<SaleHistoryItem> list = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.totalBuyHistoryByDateOfUser(d, userId));

            list.add(item);
        }

        return list;
    }

    public List<SaleHistoryItem> getSaleHistoryByMonth() {
        List<SaleHistoryItem> list = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.totalEarnByDate(d));

            list.add(item);
        }

        return list;
    }

    public List<SaleHistoryItem> getSaleHistoryByWeek() {
        List<SaleHistoryItem> list = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            SaleHistoryItem item = new SaleHistoryItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.totalEarnByDate(d));

            list.add(item);
        }

        return list;
    }

    public List<ReportItem> reportOrder(Integer type) throws OrderStatusNotFoundException {

        List<ReportItem> list = new ArrayList<>();

        List<OrderStatus> listOrderStatus = orderStatusService.listOrderStatus();

        for(OrderStatus os : listOrderStatus) {
            ReportItem item = new ReportItem();
            item.setName(os.getName());
            switch (type){
                case 0: { // year
                    item.setValue(countOrderByYearAndStatusId(os.getId()));
                    break;
                }
                case 1: { //month
                    item.setValue(countOrderByMonthAndStatusId(os.getId()));
                    break;
                }
                case 2: { //year
                    item.setValue(countOrderByWeekAndStatusId(os.getId()));
                    break;
                }
            }

            list.add(item);
        }

        return list;
    }

    public long countOrderByWeekAndStatusId(Integer statusId) throws OrderStatusNotFoundException {
        OrderStatus orderStatus = orderStatusService.getOrderStatusById(statusId);
        return reportRepository.countOrderByWeekAndStatusId(orderStatus.getId());
    }

    public long countOrderByMonthAndStatusId(Integer statusId) throws OrderStatusNotFoundException {
        OrderStatus orderStatus = orderStatusService.getOrderStatusById(statusId);
        return reportRepository.countOrderByMonthAndStatusId(orderStatus.getId());
    }

    public long countOrderByYearAndStatusId(Integer statusId) throws OrderStatusNotFoundException {
        OrderStatus orderStatus = orderStatusService.getOrderStatusById(statusId);
        return reportRepository.countOrderByYearAndStatusId(orderStatus.getId());
    }

    public List<ReportItem> getOverviewUserByWeek() {
        List<ReportItem> list = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            ReportItem item = new ReportItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.countUserByDate(d));

            list.add(item);
        }

        return list;
    }

    public List<ReportItem> getOverviewUserByMonth() {
        List<ReportItem> list = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            ReportItem item = new ReportItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.countUserByDate(d));

            list.add(item);
        }

        return list;
    }


    public List<ReportItem> getOverviewUserByYear() {

        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH) + 1;

        YearMonth current = YearMonth.of(currYear, currMonth);


        List<ReportItem> list = new ArrayList<>();


        for (int i = 11; i >= 0; i--) {
            ReportItem item = new ReportItem();

            YearMonth yearMonth = current.minusMonths(i);

            int month = yearMonth.getMonth().getValue();
            int year = yearMonth.getYear();
            item.setName(capitalizeFirstLetter(yearMonth.getMonth().toString().toLowerCase(Locale.ROOT)) + ", " + year);
            item.setValue(reportRepository.countUserInMonth(month, year));

            list.add(item);
        }

        return list;
    }


    public List<ReportItem> getOverviewReviewByWeek() {
        List<ReportItem> list = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            ReportItem item = new ReportItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.countReviewByDate(d));

            list.add(item);
        }

        return list;
    }

    public List<ReportItem> getOverviewReviewByMonth() {
        List<ReportItem> list = new ArrayList<>();

        for (int i = 29; i >= 0; i--) {
            ReportItem item = new ReportItem();
            Date d = subDays(new Date(), i);

            item.setName(convertDayToString(d));
            item.setValue(reportRepository.countReviewByDate(d));

            list.add(item);
        }

        return list;
    }


    public List<ReportItem> getOverviewReviewByYear() {

        Calendar c = Calendar.getInstance();
        int currYear = c.get(Calendar.YEAR);
        int currMonth = c.get(Calendar.MONTH) + 1;

        YearMonth current = YearMonth.of(currYear, currMonth);


        List<ReportItem> list = new ArrayList<>();


        for (int i = 11; i >= 0; i--) {
            ReportItem item = new ReportItem();

            YearMonth yearMonth = current.minusMonths(i);

            int month = yearMonth.getMonth().getValue();
            int year = yearMonth.getYear();
            item.setName(capitalizeFirstLetter(yearMonth.getMonth().toString().toLowerCase(Locale.ROOT)) + ", " + year);
            item.setValue(reportRepository.countReviewInMonth(month, year));

            list.add(item);
        }

        return list;
    }


    public List<SoldByCategoryItem> getSoldByCategory() {

        List<SoldByCategoryItem> list = new ArrayList<>();
        List<Object[]> results = reportRepository.getSoldByCategory();

        for (Object[] result : results) {
            SoldByCategoryItem item = new SoldByCategoryItem();
            item.setName((String) result[0]);
            item.setTotalSold(((BigDecimal) result[1]).doubleValue());
            list.add(item);
        }

        return list;
    }




    public Date subDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    private String convertDayToString(Date date) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(date);
    }

    public static String capitalizeFirstLetter(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
