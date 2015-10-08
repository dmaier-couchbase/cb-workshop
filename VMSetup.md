# VM Setup

The following instructions can be used in order to set up the workshop VM-s. 

We are using the following versions of Software for these preperations:

* VirtualBox >4.3: https://www.virtualbox.org/wiki/Downloads
* CentOS 6.7: http://mirror2.hs-esslingen.de/centos/6.7/isos/x86_64/CentOS-6.7-x86_64-LiveCD.iso
* Couchbase: http://packages.couchbase.com/releases/4.0.0/couchbase-server-enterprise-4.0.0-centos6.x86_64.rpm

## Couchbase Server Instance $i

### VM Installation

The place holder ${i} is the id of the instance. We need 3 VM-s for the workshop, so ${i} is from [1,2,3].

Please perform the following steps in order to provide a CentOS6 VM:

* Download the CentOS image
* Create a new VirtualBox VM with 
  * the name 'CentOS6-DCJW-Node${i}' 
  * with the type Linux/Red Hat (64 bit)
  * with 2048MB RAM
  * an empty HDD
  * with a VDMI disk format
  * and a dynamically allocated size of 20GB
* Change the VM settings
  * Network: The first network adapter uses 'NAT'
  * Storage: Choose the CentOS iso image as a CDROM drive
* Start the VM
* Install CentOS 6
  * Select 'Install text mode' in the boot menu
  * Choose the installation language as 'English'
  * Choose the keyboard layout
  * Select 'Re-initialize all'
  * Pick a time zone
  * Enter the root password 'couchbase' twice
  * Use the entire drive and write the changes to disk
  * Wait until the installation completed
  * Power off the machine after the installation
* Change VM the settings again 
  * Storage: Disabling the CDROM drive by removing the image
* Start the VM and wait until started
* Quit the initial setup wizard
* Log-in to the CLI as root

### Network config

The network configuration is a bit more complicated with Virtualbox. What we need is a VM which can reach the outside world and which can be reached from the outside world. In order to achieve this we will need to define in sum 2 virtual networks for our VM. So far we already have defined the NAT (Network Address Translation) network. NAT allows to access the outside workd from a VM. Imagine that your VM is connected to a service which acts like a router whereby the VM can reach the outside world bit can NOT be reached from the outside world or from other VM-s. In order to enable access from the outside world via NAT port forwarding can be used. So to simplify further configuration steps it makes sense to allow the access from the outside world to the VM via NAT and port forwarding. Under the network settings of the VM's NAT network define the following port forwardings:

TODO

* Change the VM settings for the NAT network
  * Network: Enable port forwarding by mapping the host port 9${i}22 to the guest port 22
  * Network: Enable port forwarding by mapping the host port 9${i}91 to the guest port 8091
  * Network: Enable port forwarding by mapping the host port 9${i}59 to the guest port 5901
* Configure and check the network
  * Get the current network settings
    * Did you get an IP address assigned? Check via 'ifconfig' and note it as $previous_ip!
    * Get the current name sever by using 'cat /etc/resolv.conf' and note it as $previous_dns!
    * Use the command 'route' to find out what the default gateway is and note it as $previous_gw!
  * Enable connectivity
    * Enable 'sshd' by executing 'chkconfig sshd on' and then reboot
    * Make sure that the Firewall is temp. disabled '/etc/init.d/iptables stop'
    * Try to connect via ssh or Putty 'ssh root@$vm_host -p 9${i}22'
    * Run 'vncserver' and use 'couchbase' as the password
  * Configure a static IP for the NAT network
    * If you installed from the LiveDVD then NetworkManager is used for the network configuration
    * Check if NetworkManager is running by executing 'service NetworkManager status'
    * Connect with a VNCClient to $vm_host:9${i}59
    * Go to the NetworkManager icon in the upper right corner and right click on it
    * Choose 'Edit Connections'
    * Click on 'Edit'
    * Go to the tab 'IPv4 Settings'
    * Choose 'Manual' as the method
    * Add the address '${previous_ip}+10/255.255.255.0/$previous_gw'! 
       * If the IP ${previous_ip}+10 is already taken then assign the next one and so on.
       * ${previus_ip}+10 means that you use 10.0.2.25 instead of the previous one which was 10.0.2.15 
    * Use the $previous_dns as DNS server
    * Reboot and test if you can still access the machine via SSH and if you can still ping the outside world from the machine
       * If the port forwarding is no longer working then you might have to adapt it regarding the new static guest IP
   * Configure a static IP for a second 'Host only' network
       * Power off the machine
       * Under settings add 'Adapter 2' and enable it
       * Select 'Host-only Adapter' as the type
       * If there is no global host-only network then you need to create it via the general VBox preferences
       * Power on the maching
       * Assign a static IP to the machine similar as before but in the range of the Host-only network
       * Check if you now can directly connect from the host
       * Check if you now can directly connect from other hosts in the same host-only network
* In the VM download additional dependencies
  * Execute 'yum install openssl'
  * The package 'wget' is already installed
  * Download the Couchbase Server RPM and place it under '/root/Downloads' by using wget

  
## Development Instance

* Perform the same steps as for the Couchbase Server VM-s BUT
   * Name the machine CentOS6-DCJW-Dev 
   * By performing a graphical installation this time, which means not to choose 'Install text mode' but just 'Install'
   * Create an user 'couchbase' with password 'couchbase' as part of the setup wizard 
   * Don't download the same dependencies, other software will be installed
   * It's especially important that the Development machine is in the same NAT and host-only network
* Install the Development environment
   * TODO 
   * 
