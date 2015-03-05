package com.example.smartwatchtablet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class BTInterface 
{
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice = null;
	OutputStream mmOutputStream;
	InputStream mmInputStream;
	Thread workerThread;
	byte[] readBuffer;
	int readBufferPosition;
	int counter;
	volatile boolean stopWorker;
	volatile float currentValue = 0;

	@SuppressLint("NewApi")
	public void connect()
	{
		System.out.println("Yo about to find");
		findBT();

		while(1 < 2)
		{
			try 
			{

				System.out.println("Yo about to open");
				openBT();
			}
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
			if(mmSocket.isConnected())
			{
				break;
			}
		}
	}

	void findBT()
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null)
		{
			//myLabel.setText("No bluetooth adapter available");
		}

		if(!mBluetoothAdapter.isEnabled())
		{
			mBluetoothAdapter.enable();
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if(device.getName().equals("HC-06")) 
				{
					mmDevice = device;

					//Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
		if(mmDevice == null)
		{
			//Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show();
		}
	}


	@SuppressLint("NewApi")
	void openBT() throws IOException
	{
		System.out.println("Yo in open");
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID


		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
		mmSocket.connect();
		if(mmSocket.isConnected() == false)
		{
			//Toast.makeText(getApplicationContext(), "getting socket failed, try again",Toast.LENGTH_SHORT).show();
			System.out.println("Yo socket not connected");
		}
		else
		{
			//Toast.makeText(getApplicationContext(), "got socket",Toast.LENGTH_SHORT).show();
			System.out.println("Yo socket connected");
		}
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();

		beginListenForData();

		//myLabel.setText("Bluetooth Opened");
	}

	void beginListenForData()
	{
		final Handler handler = new Handler(); 
		final byte delimiter = 10; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{                
				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					try 
					{
						int bytesAvailable = mmInputStream.available();                        
						if(bytesAvailable > 0)
						{
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for(int i=0;i<bytesAvailable;i++)
							{
								byte b = packetBytes[i];
								if(b == delimiter)
								{
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									handler.post(new Runnable()
									{
										public void run()
										{
											try
											{
												currentValue = Float.parseFloat(data);
											}
											catch(NumberFormatException NFE)
											{
												NFE.printStackTrace();
											}
											//myLabel.setText(data);
										}
									});
								}
								else
								{
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					} 
					catch (IOException ex) 
					{
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}


	void sendData(String msg) throws IOException
	{
		msg += "\n";
		//mmOutputStream.write(70);
		System.out.println("the mesge is "+msg);
		mmOutputStream.write(msg.getBytes());
		//myLabel.setText("Data Sent");
	}

	void closeBT() throws IOException
	{
		stopWorker = true;
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
		//myLabel.setText("Bluetooth Closed");
		//finish();
	}
}
