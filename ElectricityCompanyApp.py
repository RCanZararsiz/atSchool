#Constants
INDUSTRY_SINGLE_TIME_FEE = 305.3828
INDUSTRY_DAYTIME_FEE = 309.1833
INDUSTRY_PEAK_FEE = 490.9037 
INDUSTRY_NIGHT_FEE = 162.5171
INDUSTRY_UNIT_FEE = 64.7998
INDUSTRY_ECT_RATE = 0.01
INDUSTRY_VAT_RATE = 0.2
PUBLIC_PRIVATE_OTHER_LOW_SINGLE_TIME_FEE = 191.2220 
PUBLIC_PRIVATE_OTHER_DAYTIME_FEE = 285.8616 
PUBLIC_PRIVATE_OTHER_PEAK_FEE = 458.8843
PUBLIC_PRIVATE_OTHER_NIGHT_FEE = 148.1941
PUBLIC_PRIVATE_OTHER_UNIT_FEE = 87.8175
PUBLIC_PRIVATE_OTHER_ECT_RATE = 0.05
PUBLIC_PRIVATE_OTHER_VAT_RATE = 0.2
PUBLIC_PRIVATE_OTHER_HIGH_SINGLE_TIME_FEE = 282.8414
RESIDENTIAL_LOW_SINGLE_FEE = 48.2187
RESIDENTIAL_DAYTIME_FEE = 115.7700
RESIDENTIAL_PEAK_FEE = 208.3645
RESIDENTIAL_NIGHT_FEE = 41.7225
RESIDENTIAL_UNIT_FEE = 85.8883 
RESIDENTIAL_ECT_RATE = 0.05
RESIDENTIAL_VAT_RATE = 0.1
RESIDENTIAL_HIGH_SINGLE_FEE = 113.2271 
RESIDENTIAL_VET_MAR_SINGLE_TIME_FEE = 6.1590 
RESIDENTIAL_VET_MAR_UNIT_FEE = 58.2521
RESIDENTIAL_VET_MAR_ECT_RATE = 0.05
RESIDENTIAL_VET_MAR_VAT_RATE = 0.1
AGRICULTURAL_SINGLE_FEE = 165.3096
AGRICULTURAL_DAYTIME_FEE = 170.4822
AGRICULTURAL_PEAK_FEE = 280.0325 
AGRICULTURAL_NIGHT_FEE = 77.1882
AGRICULTURAL_UNIT_FEE = 72.1579 
AGRICULTURAL_ECT_RATE = 0.05
AGRICULTURAL_VAT_RATE = 0.1
LIGHTING_SINGLE_FEE = 259.5835 
LIGHTING_UNIT_FEE = 84.1099
LIGHTING_ECT_RATE = 0.05
LIGHTING_VAT_RATE = 0.2
#Variables
res_count = 0
res_count_m = 0
vet_count = 0
res_max_vet = 0
res_max_value = 0
res_total_time = 0
res_total_reading_dates = 0 
res_max_avarage = 0
res_loss_count = 0
res_m_loss_count = 0

pub_count = 0
pub_count_s = 0
pub_count_m = 0
pub_max_value = 0
pub_total_time = 0
pub_s_total_time = 0
pub_m_total_time = 0
pub_total_reading_dates = 0
pub_s_total_reading_dates = 0
pub_m_total_reading_dates = 0
pub_loss_count = 0
pub_m_loss_count = 0

agri_count = 0
agri_count_m = 0
agri_loss_count = 0
agri_max_value = 0
agri_total_time = 0
agri_total_reading_dates = 0
agri_m_loss_count = 0

in_loss_count = 0
in_count_m = 0
in_count = 0
in_m_loss_count = 0
in_max_value = 0
in_total_time = 0
in_total_reading_dates = 0
in_10000_consumer = 0

lig_count = 0
lig_max_value = 0
lig_total_time = 0
lig_total_reading_dates = 0
highest_total_value = 0
total_distribution_value = 0
total_ect_value = 0
total_vat_value = 0
consumer_no = 1
veteran_martry ="n"
# 85.- 131. getting general consumer info
while consumer_no != 0: #valid input loop, get consumer number
    consumer_no = int(input("Enter consumer number(enter 0 to exit): "))
    if consumer_no == 0:
        break
    while consumer_no < 0:
        consumer_no = int(input("Please enter an integer bigger than 0:"))
    
    consumer_type_code = input("Please enter your consumer type (I/i/P/p/R/r/A/a/L/l): ") #valid input loop, get consumer type code for each consumer
    while consumer_type_code != 'I' and consumer_type_code!='i' and consumer_type_code!='P' and consumer_type_code!='p' and consumer_type_code!='R' and consumer_type_code!='r' and consumer_type_code!='A' and consumer_type_code!='a' and consumer_type_code!='L' and consumer_type_code!='l':
        consumer_type_code = input("Please enter valid information: ")
    
    if consumer_type_code=='R' or consumer_type_code=='r':
        veteran_martry = input("is the consumer the family of a martyr or veteran(Y/y/N/n): ")
        while veteran_martry != 'Y' and veteran_martry != 'y' and veteran_martry != 'N' and veteran_martry != 'n': #valid input loop
            veteran_martry = input("Please enter valid information: ")
    
    if consumer_type_code=='P' or consumer_type_code=='p' or consumer_type_code=='R' or consumer_type_code=='r' or consumer_type_code == 'I' or consumer_type_code=='i' or consumer_type_code=='A' or consumer_type_code=='a':
        if veteran_martry == "N" or veteran_martry == "n":
            preffered_tariff = input("Single-time or multi-time(S/s/M/m): ")
            while preffered_tariff!='S' and preffered_tariff!='s' and preffered_tariff!='M' and preffered_tariff!='m': #valid input loop
                preffered_tariff = input("please enter valid information: ")

    prev_daytime = int(input("Previous daytime period meter value(0 or greater than 0): ")) # 
    cur_daytime = int(input(f"enter current daytime period meter value(bigger or equal than {prev_daytime}) :"))
    while prev_daytime > cur_daytime or prev_daytime < 0 : #valid input loop
        print("Invalid value please enter correctly!")
        prev_daytime = int(input("Previous daytime period meter value(0 or greater than 0): "))
        cur_daytime = int(input(f"enter current daytime period meter value(bigger or equal than {prev_daytime}) :"))
    daytime = cur_daytime - prev_daytime
    
    prev_peak = int(input("Previous peak period meter value(0 or greater than 0): "))
    cur_peak = int(input(f"enter current peak period meter value(bigger or equal than {prev_peak}) :"))
    while prev_peak > cur_peak or prev_peak < 0: #valid input loop
        print("Invalid value please enter correctly!")
        prev_peak = int(input("Previous peak period meter value(0 or greater than 0): "))
        cur_peak = int(input(f"enter current peak period meter value(bigger or equal than {prev_peak}) :"))
    peak = cur_peak - prev_peak

    prev_night = int(input("Previous night period meter value(0 or greater than 0): "))
    cur_night = int(input(f"enter current night period meter value(bigger or equal than {prev_night}) :"))
    while prev_night > cur_night or prev_night < 0: #valid input loop
        print("Invalid value please enter correctly!")
        prev_night = int(input("Previous night period meter value(0 or greater than 0): "))
        cur_night = int(input(f"enter current night period meter value(bigger or equal than {prev_night}) :"))
    night = cur_night - prev_night
    total_time = daytime + peak + night
# 132.-140. how much electricity the consumer spends
    reading_dates = int(input("Number of days between previous and current meter reading dates: integer (greater than 0) :"))
    while reading_dates <= 0: #valid input loop
        print("invalid value!!!")
        reading_dates = int(input("Number of days between previous and current meter reading dates: integer (greater than 0) :"))
    year_total = int(input("Total amount of electricity consumption in the current year until this period: integer (0 or greater than 0) :"))
    while year_total < 0: #valid input loop
        print("invalid value!!!")
        year_total = int(input("Total amount of electricity consumption in the current year until this period: integer (0 or greater than 0) :"))
# 141.-178. calculations of the consumer's type of residential   
    if consumer_type_code == 'R' or consumer_type_code == 'r':
        res_count+=1
        if veteran_martry=='Y' or veteran_martry=='y':
            vet_count+=1
            tax_free = total_time * RESIDENTIAL_VET_MAR_SINGLE_TIME_FEE
            distribution_fee = total_time * RESIDENTIAL_VET_MAR_UNIT_FEE
            ect_value = tax_free * RESIDENTIAL_VET_MAR_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * RESIDENTIAL_VET_MAR_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value
        else:
            if preffered_tariff=='S' or preffered_tariff=='s':
                if (total_time/reading_dates) <= 8:
                    tax_free = total_time * RESIDENTIAL_LOW_SINGLE_FEE
                else:
                    tax_free = ((8 * RESIDENTIAL_LOW_SINGLE_FEE) + (((total_time/reading_dates) - 8) * RESIDENTIAL_HIGH_SINGLE_FEE))*reading_dates
                distribution_fee = total_time * RESIDENTIAL_UNIT_FEE
                ect_value = tax_free * RESIDENTIAL_ECT_RATE
                vat_value = (ect_value + tax_free + distribution_fee) * RESIDENTIAL_VAT_RATE
                total_value = tax_free + distribution_fee + ect_value + vat_value

            else:
                res_count_m+=1
                tax_free_daytime = daytime * RESIDENTIAL_DAYTIME_FEE
                tax_free_peak = peak * RESIDENTIAL_PEAK_FEE
                tax_free_night =    night * RESIDENTIAL_NIGHT_FEE
                tax_free = tax_free_daytime + tax_free_night + tax_free_peak
                distribution_fee = total_time * RESIDENTIAL_UNIT_FEE
                ect_value = tax_free * RESIDENTIAL_ECT_RATE
                vat_value = (ect_value + tax_free + distribution_fee) * RESIDENTIAL_VAT_RATE
                total_value = tax_free + distribution_fee + ect_value + vat_value    
            
        res_total_time+=total_time
        res_total_reading_dates += reading_dates
        if (total_time/reading_dates)> res_max_avarage:
            res_max_avarage = (total_time/reading_dates)
            res_max_avarage_consumer_no = consumer_no
            res_max_avarage_total_time = total_time
            res_max_avarage_total_value = total_value
#179.-210. calculations of the consumer's type of public and private services sector and other    
    elif consumer_type_code=='P' or consumer_type_code=='p':
        pub_count += 1
        if preffered_tariff == 'S' or preffered_tariff == 's':
            pub_count_s += 1
            if (total_time/reading_dates) <= 30:
                tax_free = total_time * PUBLIC_PRIVATE_OTHER_LOW_SINGLE_TIME_FEE
            else:
                tax_free = ((30 * PUBLIC_PRIVATE_OTHER_LOW_SINGLE_TIME_FEE) + (((total_time/reading_dates) - 30) * PUBLIC_PRIVATE_OTHER_HIGH_SINGLE_TIME_FEE))*reading_dates
            
            distribution_fee = total_time * PUBLIC_PRIVATE_OTHER_UNIT_FEE
            ect_value = tax_free * PUBLIC_PRIVATE_OTHER_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * PUBLIC_PRIVATE_OTHER_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value
            pub_s_total_time += total_time
            pub_s_total_reading_dates += reading_dates

        else:
            pub_count_m += 1
            tax_free_daytime = daytime * PUBLIC_PRIVATE_OTHER_DAYTIME_FEE
            tax_free_peak = peak * PUBLIC_PRIVATE_OTHER_PEAK_FEE
            tax_free_night =    night * PUBLIC_PRIVATE_OTHER_NIGHT_FEE
            tax_free = tax_free_daytime + tax_free_night + tax_free_peak
            distribution_fee = total_time * PUBLIC_PRIVATE_OTHER_UNIT_FEE
            ect_value = tax_free * PUBLIC_PRIVATE_OTHER_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * PUBLIC_PRIVATE_OTHER_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value 
            pub_m_total_time += total_time
            pub_m_total_reading_dates += reading_dates 

        pub_total_time += total_time
        pub_total_reading_dates += reading_dates
#211.-232. calculations of the consumer's type of agricultural activities
    elif consumer_type_code=='A' or consumer_type_code=='a':
        agri_count+=1
        if preffered_tariff=='S' or preffered_tariff=='s':
            tax_free = total_time * AGRICULTURAL_SINGLE_FEE
            distribution_fee = total_time * AGRICULTURAL_UNIT_FEE
            ect_value = tax_free * AGRICULTURAL_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * AGRICULTURAL_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value

        else:
            agri_count_m+=1
            tax_free_daytime = daytime * AGRICULTURAL_DAYTIME_FEE
            tax_free_peak = peak * AGRICULTURAL_PEAK_FEE
            tax_free_night =    night * AGRICULTURAL_NIGHT_FEE
            tax_free = tax_free_daytime + tax_free_night + tax_free_peak
            distribution_fee = total_time * AGRICULTURAL_UNIT_FEE
            ect_value = tax_free * AGRICULTURAL_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * AGRICULTURAL_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value  

        agri_total_time += total_time
        agri_total_reading_dates += reading_dates
#233.-256. calculations of the consumer's type of Industry           
    elif consumer_type_code=='I' or consumer_type_code=='i':
        in_count +=1
        if preffered_tariff=='S' or preffered_tariff=='s':
            tax_free = total_time * INDUSTRY_SINGLE_TIME_FEE
            distribution_fee = total_time * INDUSTRY_UNIT_FEE
            ect_value = tax_free * INDUSTRY_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * INDUSTRY_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value

        else:
            in_count_m+=1
            tax_free_daytime = daytime * INDUSTRY_DAYTIME_FEE
            tax_free_peak = peak * INDUSTRY_PEAK_FEE
            tax_free_night =    night * INDUSTRY_NIGHT_FEE
            tax_free = tax_free_daytime + tax_free_night + tax_free_peak
            distribution_fee = total_time * INDUSTRY_UNIT_FEE
            ect_value = tax_free * INDUSTRY_ECT_RATE
            vat_value = (ect_value + tax_free + distribution_fee) * INDUSTRY_VAT_RATE
            total_value = tax_free + distribution_fee + ect_value + vat_value  

        in_total_time += total_time
        in_total_reading_dates += reading_dates
        if total_time>10000 or (total_value*0.01)>100000:
            in_10000_consumer += 1
#257.-267. calculations of the consumer's type of lighting
    else:
        lig_count += 1
        tax_free = total_time * LIGHTING_SINGLE_FEE
        distribution_fee = total_time * LIGHTING_UNIT_FEE
        ect_value = tax_free * LIGHTING_ECT_RATE
        vat_value = (ect_value + tax_free + distribution_fee) * LIGHTING_VAT_RATE
        total_value = tax_free + distribution_fee + ect_value + vat_value

        lig_total_time += total_time
        lig_total_reading_dates += reading_dates
#278.-373. profit/loss calculations of users and counting of consumers who have suffered losses   
    if consumer_type_code == 'I' or consumer_type_code=='i' or consumer_type_code=='P' or consumer_type_code=='p' or consumer_type_code=='A' or consumer_type_code=='a' or consumer_type_code=='L' or consumer_type_code=='l':
        if total_value>highest_total_value:
            highest_total_value = total_value
            highest_consumer_type = consumer_type_code
            highest_consumer_no = consumer_no
            highest_usage_avarage = total_time/reading_dates
            highest_total_value = total_value

    if veteran_martry =="n" or veteran_martry=="N" :
        if (consumer_type_code =="R" or consumer_type_code == "r") and (preffered_tariff == "s" or preffered_tariff == "S"):
            res_tax_free_daytime = daytime * RESIDENTIAL_DAYTIME_FEE
            res_tax_free_peak = peak * RESIDENTIAL_PEAK_FEE
            res_tax_free_night = night * RESIDENTIAL_NIGHT_FEE
            res_tax_free = res_tax_free_daytime + res_tax_free_night + res_tax_free_peak
            res_distribution_fee = total_time * RESIDENTIAL_UNIT_FEE
            res_ect_value = res_tax_free * RESIDENTIAL_ECT_RATE
            res_vat_value = (res_ect_value + res_tax_free + res_distribution_fee) * RESIDENTIAL_VAT_RATE
            res_total_value = res_tax_free + res_distribution_fee + res_ect_value + res_vat_value
            profit_amount = res_total_value - total_value
        elif (consumer_type_code =="R" or consumer_type_code == "r") and (preffered_tariff == "m" or preffered_tariff == "M") :
            if (total_time/reading_dates) <= 8:
                res_tax_free = (total_time) * RESIDENTIAL_LOW_SINGLE_FEE
            else:
                res_tax_free = ((8 * RESIDENTIAL_LOW_SINGLE_FEE) + (((total_time/reading_dates) - 8) * RESIDENTIAL_HIGH_SINGLE_FEE))*reading_dates
            
            res_distribution_fee = total_time * RESIDENTIAL_UNIT_FEE
            res_ect_value = res_tax_free * RESIDENTIAL_ECT_RATE
            res_vat_value = (res_ect_value + res_tax_free + res_distribution_fee) * RESIDENTIAL_VAT_RATE
            res_total_value = res_tax_free + res_distribution_fee + res_ect_value + res_vat_value
            profit_amount = res_total_value - total_value
            if total_value>res_total_value:
                res_loss_count+=1
                res_m_loss_count+=1

        elif (consumer_type_code =="p" or consumer_type_code == "P") and (preffered_tariff == "s" or preffered_tariff == "S"):
            pub_tax_free_daytime = daytime * PUBLIC_PRIVATE_OTHER_DAYTIME_FEE
            pub_tax_free_peak = peak * PUBLIC_PRIVATE_OTHER_PEAK_FEE
            pub_tax_free_night = night * PUBLIC_PRIVATE_OTHER_NIGHT_FEE
            pub_tax_free = pub_tax_free_daytime + pub_tax_free_night + pub_tax_free_peak
            pub_distribution_fee = total_time * PUBLIC_PRIVATE_OTHER_UNIT_FEE
            pub_ect_value = pub_tax_free * PUBLIC_PRIVATE_OTHER_ECT_RATE
            pub_vat_value = (pub_ect_value + pub_tax_free + pub_distribution_fee) * PUBLIC_PRIVATE_OTHER_VAT_RATE
            pub_total_value = pub_tax_free + pub_distribution_fee + pub_ect_value + pub_vat_value
            profit_amount = pub_total_value - total_value
        elif (consumer_type_code =="p" or consumer_type_code == "P") and (preffered_tariff == "m" or preffered_tariff == "M"):
            if (total_time/reading_dates) <= 30:
                pub_tax_free = (total_time) * PUBLIC_PRIVATE_OTHER_LOW_SINGLE_TIME_FEE
            else:
                pub_tax_free = ((30 * PUBLIC_PRIVATE_OTHER_LOW_SINGLE_TIME_FEE) + (((total_time/reading_dates) - 30) * PUBLIC_PRIVATE_OTHER_HIGH_SINGLE_TIME_FEE))*reading_dates
            
            pub_distribution_fee = total_time * PUBLIC_PRIVATE_OTHER_UNIT_FEE
            pub_ect_value = pub_tax_free * PUBLIC_PRIVATE_OTHER_ECT_RATE
            pub_vat_value = (pub_ect_value + pub_tax_free + pub_distribution_fee) * PUBLIC_PRIVATE_OTHER_VAT_RATE
            pub_total_value = pub_tax_free + pub_distribution_fee + pub_ect_value + pub_vat_value
            profit_amount = pub_total_value - total_value
            if total_value>pub_total_value:
                pub_loss_count+=1
                pub_m_loss_count +=1

        elif (consumer_type_code =="a" or consumer_type_code == "A") and (preffered_tariff == "s" or preffered_tariff == "S"):
            agri_tax_free_daytime = daytime * AGRICULTURAL_DAYTIME_FEE
            agri_tax_free_peak = peak * AGRICULTURAL_PEAK_FEE
            agri_tax_free_night =    night * AGRICULTURAL_NIGHT_FEE
            agri_tax_free = agri_tax_free_daytime + agri_tax_free_night + agri_tax_free_peak
            agri_distribution_fee = total_time * AGRICULTURAL_UNIT_FEE
            agri_ect_value = agri_tax_free * AGRICULTURAL_ECT_RATE
            agri_vat_value = (agri_ect_value + agri_tax_free + agri_distribution_fee) * AGRICULTURAL_VAT_RATE
            agri_total_value = agri_tax_free + agri_distribution_fee + agri_ect_value + agri_vat_value 
            profit_amount = agri_total_value - total_value
        elif (consumer_type_code =="a" or consumer_type_code == "A") and (preffered_tariff == "m" or preffered_tariff == "M"):
            agri_tax_free = total_time * AGRICULTURAL_SINGLE_FEE
            agri_distribution_fee = total_time * AGRICULTURAL_UNIT_FEE
            agri_ect_value = agri_tax_free * AGRICULTURAL_ECT_RATE
            agri_vat_value = (agri_ect_value + agri_tax_free + agri_distribution_fee) * AGRICULTURAL_VAT_RATE
            agri_total_value = agri_tax_free + agri_distribution_fee + agri_ect_value + agri_vat_value
            profit_amount = agri_total_value - total_value
            if total_value>agri_total_value:
                agri_loss_count+=1
                agri_m_loss_count+=1
        
        elif (consumer_type_code =="i" or consumer_type_code == "I") and (preffered_tariff == "s" or preffered_tariff == "S"):
            in_tax_free_daytime = daytime * INDUSTRY_DAYTIME_FEE
            in_tax_free_peak = peak * INDUSTRY_PEAK_FEE
            in_tax_free_night =    night * INDUSTRY_NIGHT_FEE
            in_tax_free = in_tax_free_daytime + in_tax_free_night + in_tax_free_peak
            in_distribution_fee = total_time * INDUSTRY_UNIT_FEE
            in_ect_value = in_tax_free * INDUSTRY_ECT_RATE
            in_vat_value = (in_ect_value + in_tax_free + in_distribution_fee) * INDUSTRY_VAT_RATE
            in_total_value = in_tax_free + in_distribution_fee + in_ect_value + in_vat_value
            profit_amount = in_total_value - total_value
        elif (consumer_type_code =="i" or consumer_type_code == "I") and (preffered_tariff == "m" or preffered_tariff == "M"):
            in_tax_free = total_time * INDUSTRY_SINGLE_TIME_FEE
            in_distribution_fee = total_time * INDUSTRY_UNIT_FEE
            in_ect_value = in_tax_free * INDUSTRY_ECT_RATE
            in_vat_value = (in_ect_value + in_tax_free + in_distribution_fee) * INDUSTRY_VAT_RATE
            in_total_value = in_tax_free + in_distribution_fee + in_ect_value + in_vat_value
            profit_amount = in_total_value - total_value
            
            if total_value>in_total_value:
                in_loss_count+=1
                in_m_loss_count+=1
    
    if (veteran_martry =="n" or veteran_martry=="N") and not (consumer_type_code =="L" or consumer_type_code=="l") :
        if profit_amount<0:
            profit_or_loss = "Loss"
        elif profit_amount == 0:
            profit_or_loss = "There is no profit or loss."
        else:
            profit_or_loss = "Profit"

     
#375.-378. calculating fees     
    total_distribution_value += distribution_fee
    total_ect_value += ect_value
    total_vat_value += vat_value
# outputs in while loop for each consumer
    print(f"Consumer No: {consumer_no}")
    print(f"Consumer Type Code: {consumer_type_code}")
    print(f"Daytime period electricity consumption amount in this period (kWh): {daytime}")
    print(f"Peak period electricity consumption amount in this period (kWh): {peak}")
    print(f"Night period electricity consumption amount in this period (kWh): {night}")
    print(f"Total electricity consumption amount in this period (kWh): {total_time}")
    print(f"Total electricity consumption fee for this period (TL): {tax_free*0.01:.2f}")
    print(f"ECT amount to be transferred to the municipality this period (TL): {ect_value*0.01:.2f}")
    print(f"VAT amount to be transferred to the state this period (TL): {vat_value*0.01:.2f}")
    print(f"Total invoice amount for this period (TL): {total_value*0.01:.2f}")
    if (veteran_martry =="n" or veteran_martry=="N") and  not (consumer_type_code =="L" or consumer_type_code=="l") :
        print(f"Consumer's (family of martyrs or veterans type and lighting type) profit/loss situation: {profit_or_loss}\nConsumer's (family of martyrs or veterans type and lighting type) profit/loss amount(TL): {profit_amount*0.01:.2f} ")
    print(f"Total electricity consumption amount in the current year as of this billing period (kWh): {year_total + total_time}")
    if year_total + total_time < 1000:
        print("Consumer does not deserve to be a free consumer")
    else:
        print("Consumer deserve to be a free consumer")



if in_count>0:
    in_10000_consumer_percentage = in_10000_consumer * 100 / in_count

# outputs out of while loop for every consumer's statistics
total_consumer = res_count + pub_count + in_count + agri_count + lig_count
if total_consumer>0:
    print(f"number of residential consumers: {res_count}\nnumber of public and private services sector and other consumers: {pub_count}\nnumber of agricultural consumers:{agri_count}\nnumber of industry consumers:{in_count}\nnumber of lighting consumers:{lig_count}")
    print(f"percentage of residential consumers: {res_count*100/total_consumer:.2f}%\npercentage of public and private services sector and other consumers: {pub_count*100/total_consumer:.2f}%\npercentage of agricultural consumers: {agri_count*100/total_consumer:.2f}%\npercentage of industry consumers: {in_count*100/total_consumer:.2f}%\npercentage of lighting consumers: {lig_count*100/total_consumer:.2f}%")
    print(f"average of residential consumer: {res_total_time / res_count:.2f}\navarage of public and private services sector and other consumers: {pub_total_time / pub_count:.2f}\navarage of agricultural consumers: {agri_total_time / agri_count:.2f}\navarage of industry consumers: {in_total_time / in_count:.2f}\navarage of lighting consumers: {lig_total_time / lig_count:.2f}")
    print(f"total electricity usage of residential consumer: {res_total_time}\ntotal electricity usage of public and private services sector and other consumers: {pub_total_time}\ntotal electricity usage of agricultural consumers: {agri_total_time}\ntotal electricity usage of industry consumers: {in_total_time}\ntotal electricity usage of lighting consumers: {lig_total_time}")
    print(f"total electricity usage is: {res_total_time + pub_total_time + in_total_time + agri_total_time + lig_total_time}") 

    print(f"Number of public and private services sector and other type consumers who prefer single-time tariff: {pub_count_s}\nNumber of public and private services sector and other type consumers who prefer multi-time tariff: {pub_count_m}") 
    print(f"Percentage among public and private services sector and other type consumers who prefer single-time tariff: {pub_count_s*100/pub_count:.2f}%\nPercentage among public and private services sector and other type consumers who prefer multi-time tariff: {pub_count_m*100/pub_count:.2f}%")
    print(f"Average daily electricity consumption amounts in this period for public and private services and other type consumers (single-time)(kWh): {pub_s_total_time/pub_s_total_reading_dates:.2f}\nAverage daily electricity consumption amounts in this period for public and private services and other type consumers (multi-time)(kWh): {pub_m_total_time/pub_m_total_reading_dates:.2f}")
    print(f"Number of industrial consumers whose electricity consumption is more than 10000 kWh or whose electricity bill is more than 100000 TL in the relevant period:{in_10000_consumer}")
    print(f"Industrial consumers whose electricity consumption amount is more than 10000 kWh or whose electricity bill is more than 100000 TL in the relevant period percentages among industrial consumers:{in_10000_consumer_percentage:.2f}%")
    print(f"Residential consumers with the highest average daily electricity consumption, Consumer no: {res_max_avarage_consumer_no}, Consumption amount in te relevant period: {res_max_avarage_total_time}, Daily average electricity consumption amount (kWh): {res_max_avarage:.2f},  Total bill amount for this period(TL): {res_max_avarage_total_value*0.01:.2f} ")
    print(f"The consumer with the highest total bill, excluding residential consumers, Consumer no: {highest_consumer_no}, Consumer type: {highest_consumer_type}, Daily average electricity consumption amount (kWh): {highest_usage_avarage:.2f}, Total bill amount for this period(TL): {highest_total_value*0.01:.2f}")
    print(f"Total revenue amount obtained by the GDZ corporation (TL): {total_distribution_value*0.01:.2f}, Total revenue amount obtained by the Municipality (TL): {total_ect_value*0.01:.2f}, Total revenue amount obtained by the state (TL): {total_vat_value*0.01:.2f} ")
    print(f"The percentage of consumers(family of martyrs or veterans type and lighting type) who made a loss despite choosing multi-time tariff in the relevant period: {(res_m_loss_count + pub_m_loss_count + agri_m_loss_count + in_m_loss_count)*100/(res_count_m + pub_count_m + agri_count_m + in_count_m):.2f}%  ")