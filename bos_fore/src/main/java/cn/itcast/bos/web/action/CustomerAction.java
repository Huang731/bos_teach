package cn.itcast.bos.web.action;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.utils.MailUtils;
import cn.itcast.bos.utils.SmsUtils;
import cn.itcast.crm.domain.Customer;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Action(value = "customer_sendSms")
	public String sendSms() throws IOException {
		// 手机号保存到Customer对象
		// 生成短信验证码
		String randomCode = RandomStringUtils.randomNumeric(4);
		// 将短信验证码 保存到session
		ServletActionContext.getRequest().getSession().setAttribute(model.getTelephone(), randomCode);

		System.out.println("生成手机验证码为：" + randomCode);
		// 编辑短信内容
		final String msg = "你的验证码为：123456【中正云通信】";

		//调用mq服务，发送一条信息
		jmsTemplate.send("bos_sms", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", model.getTelephone());
				mapMessage.setString("msg", msg);
				return mapMessage;
			}
		});
		
		return NONE;
	}

	// 属性驱动
	private String checkcode;

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Action(value = "coustomer_regist", results = {
			@Result(name = "success", type = "redirect", location = "signup-success.html"),
			@Result(name = "input", type = "redirect", location = "signup.html") })
	public String regist() {
		// 先校验短信验证码 如果不通过 调回注册页面
		String code = (String) ServletActionContext.getRequest().getSession().getAttribute(model.getTelephone());
		if (checkcode == null || !(checkcode.equals(code))) {
			System.out.println("短信验证码错误");
			// 短信验证码错误
			return INPUT;
		}
		WebClient.create("http://localhost:8081/crm_management/services/customerService/saveCustomer")
				.type(MediaType.APPLICATION_JSON).post(model);
		System.out.println("客户注册成功！");

		// 生成激活码
		String activecode = RandomStringUtils.randomNumeric(32);

		// 将激活码保存到redis，设置24小时失效
		redisTemplate.opsForValue().set(model.getTelephone(), activecode, 24, TimeUnit.HOURS);

		String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定：<br/><a href='" + MailUtils.activeUrl + "?telephone="
				+ model.getTelephone() + "&activecode=" + activecode + "'>速运快递邮箱绑定地址</a>";
		System.out.println(content);
		System.out.println(model.getEmail());
		MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());

		return SUCCESS;
	}

	// 属性驱动
	private String activecode;

	public void setActivecode(String activecode) {
		this.activecode = activecode;
	}

	@Action(value = "customer_activeMail")
	public String avtiveMail() throws IOException {
		System.out.println(1);
		ServletActionContext.getResponse().setContentType("text/html;charset=UTF-8");
		String code = redisTemplate.opsForValue().get(model.getTelephone());
		if (code == null || !(code.equals(activecode))) {
			ServletActionContext.getResponse().getWriter().println("激活码无效，请重新登陆系统，重新绑定邮箱");
		} else {
			Customer customer = WebClient
					.create("http://localhost:8081/crm_management/services/customerService/selectCustomer/"
							+ model.getTelephone())
					.accept(MediaType.APPLICATION_JSON).get(Customer.class);

			if (customer.getType() == null || customer.getType() != 1) {
				WebClient
						.create("http://localhost:8081/crm_management/services/customerService/updateCustomer?telephone="
								+ model.getTelephone())
						.put(null);
				ServletActionContext.getResponse().getWriter().println("邮箱绑定成功！");
			} else {
				ServletActionContext.getResponse().getWriter().print("邮箱已经绑定成功，无需重复绑定");
			}
		}


		return NONE;
	}
}
