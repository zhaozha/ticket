package com.qy.ticket.service;

import com.qy.ticket.common.CommonResult;
import com.qy.ticket.dto.user.TblBillDTO;
import com.qy.ticket.dto.user.TblRefundDTO;
import com.qy.ticket.dto.user.TblSpecialRefundDTO;
import com.qy.ticket.entity.TblUser;

/**
 * @author zhaozha
 * @date 2020/1/7 上午10:59
 */
public interface UserService {
  /**
   * 微信登陆
   *
   * @param code 微信code
   * @return 统一返回
   */
  CommonResult wxLogin(String code);

  /**
   * 微信注册
   *
   * @param tblUser 用户
   * @return 统一返回
   */
  CommonResult wxRegister(TblUser tblUser);

  /**
   * 微信支付-统一下单
   *
   * @param tblBillDTO 账单
   * @return 统一返回
   */
  CommonResult unifiedorder(TblBillDTO tblBillDTO) throws Exception;

  /**
   * 微信充值回调
   *
   * @param xmlStr 微信返回xml报文
   * @return 统一返回
   */
  String wxPayConfirm(String xmlStr) throws Exception;

  /**
   * 查询票信息
   *
   * @param productId 产品编号
   * @param parkId 景区编号
   * @return 统一返回
   */
  CommonResult ticket(Long productId, Long parkId);

  /**
   * 根据手机号查行程
   *
   * @param phoneNum 手机号
   * @param status 0查询有的1全部
   * @param productId 产品编号
   * @param parkId 景区编号
   * @return 统一返回
   */
  CommonResult record(String phoneNum, Integer status, Long productId, Long parkId);

  /**
   * 查询历史行程
   *
   * @param phoneNum 手机号
   * @param date 日期
   * @param productId 产品编号
   * @param parkId 景区编号
   * @return 统一返回
   */
  CommonResult historyRecord(String phoneNum, String date, Integer status, Long productId, Long parkId);

  /**
   * 根据票数退款
   *
   * @param tblRefundDTO 退款
   * @return 统一返回
   * @throws Exception
   */
  CommonResult refund(TblRefundDTO tblRefundDTO) throws Exception;

  /**
   * 指定金额退款
   *
   * @param tblSpecialRefundDTO 指定金额退款
   * @return 统一返回
   * @throws Exception
   */
  CommonResult specialRefund(TblSpecialRefundDTO tblSpecialRefundDTO) throws Exception;

  /**
   * 核销
   *
   * @param recordId 行程号
   * @return 统一返回
   * @throws Exception
   */
  CommonResult cancellation(Long recordId);

  /**
   * 胸卡核销
   *
   * @param phoneNum 手机号
   * @return 统一返回
   * @throws Exception
   */
  CommonResult cancellationByCard(String phoneNum, Long parkId, Long productId, String id);

  /**
   * 核销记录查询
   *
   * @param phoneNum 用户手机号
   * @return 统一返回
   */
  CommonResult selectCancellation(String phoneNum);

  /**
   * 查询充值记录
   *
   * @param phoneNum 用户手机号
   * @return 统一返回
   */
  CommonResult selectBills(String phoneNum);
}
