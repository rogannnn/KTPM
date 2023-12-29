package com.luv2code.doan.service;

import com.luv2code.doan.entity.*;
import com.luv2code.doan.exceptions.AddressNotFoundException;
import com.luv2code.doan.exceptions.NotFoundException;
import java.util.List;


public interface AddressService {

    public List<Province> getListProvinces();

    public List<District> getListDistrict();

    public List<Ward> getListWard();

    public Ward getWardByCode(String code) throws NotFoundException;

    public District getDistrictByCode(String code) throws NotFoundException;

    public Province getProvinceByCode(String code) throws NotFoundException;

    public List<District> getListDistrictByProvince(String provinceCode) throws NotFoundException;

    public List<Ward> getListWardByDistrict(String districtCode) throws NotFoundException;

    public List<Address> getListAddressByUserId(Integer id);

    public Address getAddressDefaultByUserId(Integer id);

    public long getCountAddressByUserId(Integer id);

    public Address save(Address address);

    public void setDefaultAddress(Integer addressId,Integer userId);

    public void delete(Integer addressId, Integer userId);

    public Address getAddress(Integer addressId, Integer userId) throws AddressNotFoundException;
}
