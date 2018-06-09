package org.skynet.frame;

import org.skynet.frame.annotation.Path;
import org.skynet.frame.base.SkynetEntity;

public class SFDABean extends SkynetEntity{
	@Path(value="tr:eq(1) td:eq(1)")
	private String pzwh ;
	@Path(value="tr:eq(2) td:eq(1)")
	private String cpmc ;
	@Path(value="tr:eq(3) td:eq(1)")
	private String ywmc; 
	@Path(value="tr:eq(4) td:eq(1)")
	private String spm; 
	@Path(value="tr:eq(5) td:eq(1)")
	private String jx; 
	@Path(value="tr:eq(6) td:eq(1)")
	private String gg; 
	@Path(value="tr:eq(7) td:eq(1)")
	private String scdw; 
	@Path(value="tr:eq(8) td:eq(1)")
	private String scdz; 
	@Path(value="tr:eq(9) td:eq(1)")
	private String sclb; 
	@Path(value="tr:eq(10) td:eq(1)")
	private String pzrq; 
	@Path(value="tr:eq(11) td:eq(1)")
	private String oldPzwh; 
	@Path(value="tr:eq(12) td:eq(1)")
	private String bwm; 
	@Path(value="tr:eq(13) td:eq(1)")
	private String remarkOfBwm;
	
	
	public String getPzwh() {
		return pzwh;
	}
	public void setPzwh(String pzwh) {
		this.pzwh = pzwh;
	}
	public String getCpmc() {
		return cpmc;
	}
	public void setCpmc(String cpmc) {
		this.cpmc = cpmc;
	}
	public String getYwmc() {
		return ywmc;
	}
	public void setYwmc(String ywmc) {
		this.ywmc = ywmc;
	}
	public String getSpm() {
		return spm;
	}
	public void setSpm(String spm) {
		this.spm = spm;
	}
	public String getJx() {
		return jx;
	}
	public void setJx(String jx) {
		this.jx = jx;
	}
	public String getGg() {
		return gg;
	}
	public void setGg(String gg) {
		this.gg = gg;
	}
	public String getScdw() {
		return scdw;
	}
	public void setScdw(String scdw) {
		this.scdw = scdw;
	}
	public String getScdz() {
		return scdz;
	}
	public void setScdz(String scdz) {
		this.scdz = scdz;
	}
	public String getSclb() {
		return sclb;
	}
	public void setSclb(String sclb) {
		this.sclb = sclb;
	}
	public String getPzrq() {
		return pzrq;
	}
	public void setPzrq(String pzrq) {
		this.pzrq = pzrq;
	}
	public String getOldPzwh() {
		return oldPzwh;
	}
	public void setOldPzwh(String oldPzwh) {
		this.oldPzwh = oldPzwh;
	}
	public String getBwm() {
		return bwm;
	}
	public void setBwm(String bwm) {
		this.bwm = bwm;
	}
	public String getRemarkOfBwm() {
		return remarkOfBwm;
	}
	public void setRemarkOfBwm(String remarkOfBwm) {
		this.remarkOfBwm = remarkOfBwm;
	} 
}
