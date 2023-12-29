package com.luv2code.doan.paypal;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayPalClient {

    String clientId = "AVyQcnjtaDpmchNOWtYdW_mXOwYQz0caeABmOinsCmCG_ZAikhgekoMRbmgJYMw8u717Wys-gWiPSKYk";
    String clientSecret = "EHTBWmbtFmrtJ4oqv4SyRyIwTfT_3hcuf4JrBazyopnsoEb1NFrw7Z2lvClXs64I0vTnvPZI1bEHK1Lx";



//    public Map<String, Object> createPayment(String sum){
//        Map<String, Object> response = new HashMap<String, Object>();
//        Amount amount = new Amount();
//        amount.setCurrency("USD");
//        amount.setTotal(sum);
//        Transaction transaction = new Transaction();
//        transaction.setAmount(amount);
//        List<Transaction> transactions = new ArrayList<Transaction>();
//        transactions.add(transaction);
//
//        Payer payer = new Payer();
//        payer.setPaymentMethod("paypal");
//
//        Payment payment = new Payment();
//        payment.setIntent("sale");
//
//        payment.setPayer(payer);
//        payment.setTransactions(transactions);
//
//        RedirectUrls redirectUrls = new RedirectUrls();
//        redirectUrls.setCancelUrl("http://localhost:3000/cancel");
//        redirectUrls.setReturnUrl("http://localhost:3000");
//        payment.setRedirectUrls(redirectUrls);
//        Payment createdPayment;
//        try {
//            String redirectUrl = "";
//            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
//            createdPayment = payment.create(context);
//            if(createdPayment!=null){
//                List<Links> links = createdPayment.getLinks();
//                for (Links link:links) {
//                    if(link.getRel().equals("approval_url")){
//                        redirectUrl = link.getHref();
//                        break;
//                    }
//                }
//                response.put("status", "success");
//                response.put("redirect_url", redirectUrl);
//            }
//        } catch (PayPalRESTException e) {
//            System.out.println("Error happened during payment creation!");
//        }
//        return response;
//    }


    public Map<String, Object> completePayment(String paymentId, String payerId){
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment != null){
                response.put("status", "success");
                response.put("payment", createdPayment);
                System.out.println("success");
                return response;
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
        return response;
    }



}