package com.cskaoyan.gateway.controller.shopping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cskaoyan.gateway.form.shopping.CartForm;
import com.mall.commons.result.ResponseData;
import com.mall.commons.result.ResponseUtil;
import com.mall.shopping.ICartService;
import com.mall.shopping.constants.ShoppingRetCode;
import com.mall.shopping.dto.*;
import com.mall.user.intercepter.TokenIntercepter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * create by ciggar on 2019/03/11
 */
@RestController
@RequestMapping("/shopping")
@Api(tags = "CartController", description = "购物车控制层")
public class CartController {

    @Reference(timeout = 3000,check = false)
    ICartService iCartService;

    /**
     * 获得购物车列表
     *
     * @param request
     * @return
     */
    @GetMapping("/carts")
    @ApiOperation("获得购物车列表")
    public ResponseData carts(HttpServletRequest request) {
        String userInfo = (String) request.getAttribute(TokenIntercepter.USER_INFO_KEY);
        JSONObject jsonObject = JSON.parseObject(userInfo);
        long uid = Long.parseLong(jsonObject.getString("uid"));
        CartListByIdRequest cartListByIdRequest = new CartListByIdRequest();
        cartListByIdRequest.setUserId(uid);
        CartListByIdResponse response = iCartService.getCartListById(cartListByIdRequest);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getCartProductDtos());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 添加商品到购物车
     *
     * @param cartForm
     * @return
     */
    @PostMapping("/carts")
    @ApiOperation("添加商品到购物车")
    @ApiImplicitParam(name = "cartForm", value = "购物车信息", dataType = "CartForm", required = true)
    public ResponseData carts(@RequestBody CartForm cartForm) {
        AddCartRequest request = new AddCartRequest();
        request.setItemId(cartForm.getProductId());
        request.setNum(cartForm.getProductNum());
        request.setUserId(cartForm.getUserId());
        AddCartResponse response = iCartService.addToCart(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 更新购物车中的商品
     *
     * @param cartForm
     * @return
     */
    @PutMapping("/carts")
    @ApiOperation("更新购物车中的商品")
    @ApiImplicitParam(name = "cartForm", value = "购物车信息", dataType = "CartForm", required = true)
    public ResponseData updateCarts(@RequestBody CartForm cartForm) {
        UpdateCartNumRequest request = new UpdateCartNumRequest();
        request.setChecked(cartForm.getChecked());
        request.setItemId(cartForm.getProductId());
        request.setNum(cartForm.getProductNum());
        request.setUserId(cartForm.getUserId());
        UpdateCartNumResponse response = iCartService.updateCartNum(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 删除购物车中的商品
     *
     * @return
     */
    @ApiOperation("删除购物车中的商品")
    @DeleteMapping("/carts/{uid}/{pid}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户ID", paramType = "path"),
            @ApiImplicitParam(name = "pid", value = "商品ID", paramType = "path")
    })
    public ResponseData deleteCarts(@PathVariable("uid") long uid, @PathVariable("pid") long pid) {
        DeleteCartItemRequest request = new DeleteCartItemRequest();
        request.setUserId(uid);
        request.setItemId(pid);

        DeleteCartItemResponse response = iCartService.deleteCartItem(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }

    /**
     * 对购物车的全选操作
     *
     * @param cartForm
     * @return
     */
    @ApiOperation("对购物车的全选操作")
    @PutMapping("/items")
    @ApiImplicitParam(name = "cartForm", value = "购物车信息", dataType = "CartForm", required = true)
    public ResponseData checkCarts(@RequestBody CartForm cartForm) {
        CheckAllItemRequest request = new CheckAllItemRequest();
        request.setChecked(cartForm.getChecked());
        request.setUserId(cartForm.getUserId());
        CheckAllItemResponse response = iCartService.checkAllCartItem(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }


    /**
     * 删除购物车中选中的商品
     *
     * @param id
     * @return
     */
    @ApiOperation("删除购物车中选中的商品")
    @DeleteMapping("/items/{id}")
    @ApiImplicitParam(name = "id", value = "商品ID", paramType = "path")
    public ResponseData deleteCheckCartItem(@PathVariable("id") Long id) {
        DeleteCheckedItemRequest request = new DeleteCheckedItemRequest();
        request.setUserId(id);
        request.setUserId(request.getUserId());
        DeleteCheckedItemResposne response = iCartService.deleteCheckedItem(request);
        if (response.getCode().equals(ShoppingRetCode.SUCCESS.getCode())) {
            return new ResponseUtil().setData(response.getMsg());
        }
        return new ResponseUtil().setErrorMsg(response.getMsg());
    }


}
