package com.example.weatherviewer.open_weather_map;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DayWeather {

	@SerializedName("dt")
	private int dt;

	@SerializedName("sunrise")
	private int sunrise;

	@SerializedName("temp")
	private Temp temp;

	@SerializedName("sunset")
	private int sunset;

	@SerializedName("deg")
	private int deg;

	@SerializedName("weather")
	private List<WeatherNetwork> weather;

	@SerializedName("humidity")
	private int humidity;

	@SerializedName("pressure")
	private int pressure;

	@SerializedName("clouds")
	private int clouds;

	@SerializedName("feels_like")
	private FeelsLike feelsLike;

	@SerializedName("speed")
	private double speed;

	public void setDt(int dt){
		this.dt = dt;
	}

	public int getDt(){
		return dt;
	}

	public void setSunrise(int sunrise){
		this.sunrise = sunrise;
	}

	public int getSunrise(){
		return sunrise;
	}

	public void setTemp(Temp temp){
		this.temp = temp;
	}

	public Temp getTemp(){
		return temp;
	}

	public void setSunset(int sunset){
		this.sunset = sunset;
	}

	public int getSunset(){
		return sunset;
	}

	public void setDeg(int deg){
		this.deg = deg;
	}

	public int getDeg(){
		return deg;
	}

	public void setWeather(List<WeatherNetwork> weather){
		this.weather = weather;
	}

	public List<WeatherNetwork> getWeather(){
		return weather;
	}

	public void setHumidity(int humidity){
		this.humidity = humidity;
	}

	public int getHumidity(){
		return humidity;
	}

	public void setPressure(int pressure){
		this.pressure = pressure;
	}

	public int getPressure(){
		return pressure;
	}

	public void setClouds(int clouds){
		this.clouds = clouds;
	}

	public int getClouds(){
		return clouds;
	}

	public void setFeelsLike(FeelsLike feelsLike){
		this.feelsLike = feelsLike;
	}

	public FeelsLike getFeelsLike(){
		return feelsLike;
	}

	public void setSpeed(double speed){
		this.speed = speed;
	}

	public double getSpeed(){
		return speed;
	}

	@Override
 	public String toString(){
		return 
			"DayWeather{" +
			"dt = '" + dt + '\'' + 
			",sunrise = '" + sunrise + '\'' + 
			",temp = '" + temp + '\'' + 
			",sunset = '" + sunset + '\'' + 
			",deg = '" + deg + '\'' + 
			",weather = '" + weather + '\'' + 
			",humidity = '" + humidity + '\'' + 
			",pressure = '" + pressure + '\'' + 
			",clouds = '" + clouds + '\'' + 
			",feels_like = '" + feelsLike + '\'' + 
			",speed = '" + speed + '\'' + 
			"}";
		}
}