package com.example.atmapp.service;

import com.example.atmapp.entity.*;
import com.example.atmapp.entity.enums.CardName;
import com.example.atmapp.entity.enums.RoleName;
import com.example.atmapp.payload.ApiResponse;
import com.example.atmapp.payload.UserTransferDto;
import com.example.atmapp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TransferAction implements UserDetailsService {
    @Autowired

    UserRepository userRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    BankRepository bankRepository;
    @Autowired
    OutputRepository outputRepository;
    @Autowired
    AtmRepository atmRepository;
    @Autowired
    AtmBoxesRepository atmBoxesRepository;
    @Autowired
    JavaMailSender javaMailSender;


    public ResponseEntity<?> transferUZCARD(Integer atmId, UserTransferDto userTransferDto) {
        Optional<ATM> optionalATM = atmRepository.findById(atmId);
        ATM atm = optionalATM.get();
        Optional<Card> optionalCard = cardRepository.findByCardNumbeerAndSVVcode(
                userTransferDto.getCardNumbeer(),
                userTransferDto.getSVVcode()
        );
        Double summa = userTransferDto.getSumma();
        if (optionalCard.isEmpty()) {
            return ResponseEntity.status(404).body("Karta topimadi");
        }
        Card card = optionalCard.get();
        if (card.getCardType().equals(CardName.UZCARD)) {
            if (card.isAccountNonExpired()) {
                int count = 0;
                while (count < 3) {

                    if (Objects.equals(card.getPinCode(), userTransferDto.getPinCode())) {
                        int box100000 = (int) (summa / 100_000);
                        int box50_000 = (int) ((summa % 100_000) / 50_000);
                        int box10_000 = (int) ((summa % 100_000) % 50_000) / 10_000;
                        int box5_000 = (int) (((summa % 100_000) % 50_000) % 10_000) / 5000;

                        int box50000 = (int) (summa / 50_000);
                        int box10_000_1 = (int) (summa % 50_000) / 10_000;
                        int box5000_1 = (int) ((summa % 50_000) % 10_000) / 5000;


                        int box10000 = (int) (summa / 10_000);
                        int box5000_2 = (int) (summa % 10_000) / 5000;


                        int box5000 = (int) (summa / 5_000);


                        int box1000 = (int) (summa / 1000);

                        if (Objects.equals(card.getBank().getId(), atm.getBank().getId())) {

                          //  KARTA SHU BANKGA TEGISHLIK PUL YECHISHDA COMISSIYA OLINMAYDI


                            if (userTransferDto.getSumma() >= 1000 && userTransferDto.getSumma() <= 1_000_000) {


                                for (AtmBoxes atmBox : atm.getAtmBoxes()) {
                                    if (atmBox.getSum100000() >= box100000
                                            && box100000 != 0
                                            && atmBox.getSum50000() >= box50_000
                                            && atmBox.getSum10000() >= box10_000
                                            && atmBox.getSum5000() >= box5_000

                                    ) {
                                        atmBox.setSum100000(atmBox.getSum100000() - box100000);
                                        atmBox.setSum50000(atmBox.getSum50000() - box50_000);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5_000);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() >= box50000
                                            && atmBox.getSum5000() >= box10_000_1
                                            && atmBox.getSum5000() >= box5000_1
                                    ) {
                                        atmBox.setSum50000(atmBox.getSum50000() - box50000);
                                        atmBox.setSum50000(atmBox.getSum50000() - box10_000_1);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5000_1);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() >= box10_000
                                            && atmBox.getSum5000() >= box5000_2
                                    ) {
                                        atmBox.setSum10000(atmBox.getSum10000() - box10000);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5000_2);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() == 0
                                            && atmBox.getSum5000() >= box5000
                                    ) {
                                        atmBox.setSum5000(atmBox.getSum5000() - box5_000);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() == 0
                                            && atmBox.getSum5000() == 0
                                            && atmBox.getSum1000() >= box1000
                                    ) {
                                        atmBox.setSum1000(atmBox.getSum1000() - box1000);
                                    }

                                }

//                                card.setCardBalance(card.getCardBalance() - userTransferDto.getSumma());

                                double comision = userTransferDto.getSumma() * 0.01;
                                Bank bank = atm.getBank();

                                //AMALIYOT HAQIDA  MALUMOT
                                List<OutputInfo> outputInfoAtm = atm.getOutputInfo();
                                OutputInfo outputInfo=new OutputInfo();
                                outputInfo.setActionDate(new Date());
                                outputInfo.setOutputSumma(userTransferDto.getSumma());
                                outputInfo.setCards(Collections.singletonList(card));
                                outputInfoAtm.add(outputInfo);

                                card.setCardBalance(card.getCardBalance() - userTransferDto.getSumma() - comision);
                                bank.setBankAccountBalance(bank.getBankAccountBalance() + comision);

                                cardRepository.save(card);

                                return ResponseEntity.ok(new ApiResponse("Amaliyot bajarildi",outputInfo,true));
//
                            }
                        } else {


                            if (userTransferDto.getSumma() >= 1000 && userTransferDto.getSumma() <= 1_000_000) {


                                for (AtmBoxes atmBox : atm.getAtmBoxes()) {
                                    if (atmBox.getSum100000() >= box100000
                                            && box100000 != 0
                                            && atmBox.getSum50000() >= box50_000
                                            && atmBox.getSum10000() >= box10_000
                                            && atmBox.getSum5000() >= box5_000

                                    ) {
                                        atmBox.setSum100000(atmBox.getSum100000() - box100000);
                                        atmBox.setSum50000(atmBox.getSum50000() - box50_000);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5_000);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() >= box50000
                                            && atmBox.getSum5000() >= box10_000_1
                                            && atmBox.getSum5000() >= box5000_1
                                    ) {
                                        atmBox.setSum50000(atmBox.getSum50000() - box50000);
                                        atmBox.setSum50000(atmBox.getSum50000() - box10_000_1);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5000_1);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() >= box10_000
                                            && atmBox.getSum5000() >= box5000_2
                                    ) {
                                        atmBox.setSum10000(atmBox.getSum10000() - box10000);
                                        atmBox.setSum5000(atmBox.getSum5000() - box5000_2);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() == 0
                                            && atmBox.getSum5000() >= box5000
                                    ) {
                                        atmBox.setSum5000(atmBox.getSum5000() - box5_000);
                                    } else if (atmBox.getSum100000() == 0
                                            && atmBox.getSum50000() == 0
                                            && atmBox.getSum10000() == 0
                                            && atmBox.getSum5000() == 0
                                            && atmBox.getSum1000() >= box1000
                                    ) {
                                        atmBox.setSum1000(atmBox.getSum1000() - box1000);
                                    }

                                }




                                double comision = userTransferDto.getSumma() * 0.01;
                                Bank bank = atm.getBank();

                                //AMALIYOT HAQIDA  MALUMOT
                                List<OutputInfo> outputInfoAtm = atm.getOutputInfo();
                                OutputInfo outputInfo=new OutputInfo();
                                outputInfo.setActionDate(new Date());
                                outputInfo.setOutputSumma(userTransferDto.getSumma());
                                outputInfo.setCards(Collections.singletonList(card));
                                outputInfoAtm.add(outputInfo);

                                card.setCardBalance(card.getCardBalance() - userTransferDto.getSumma() - comision);
                                bank.setBankAccountBalance(bank.getBankAccountBalance() + comision);

                                cardRepository.save(card);

                                return ResponseEntity.ok(new ApiResponse("Amaliyot bajarildi",outputInfo,true));

//                                Bank bank = atm.getBank();
//                                double comision = userTransferDto.getSumma() * 0.01;
//                                card.setCardBalance(card.getCardBalance() - userTransferDto.getSumma() - comision);
//                                bank.setBankAccountBalance(bank.getBankAccountBalance() + comision);
//                                cardRepository.save(card);
                            }
                        }


                    } else count++;
                }
                if (count == 3) {
                    card.setAccountNonLocked(false);
                    cardRepository.save(card);
                    return ResponseEntity.status(HttpStatus.LOCKED).body("Pin kodni 3 marta notugri kiritdingiz karta bloklandi");
                }
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(
                        "Kartangizni amal qilish muddati  tugagan o'zingizga  yaqin bo'lgan filiallarimizga uchrashing");
            }


        }
        return ResponseEntity.status(404).body("ATM  faqat UZCARD toifadagi kartalarga xizmat qiladi");
    }

    public ResponseEntity<?> transferVisa(Integer atmId, UserTransferDto userTransferDto) {
        Optional<ATM> optionalATM = atmRepository.findById(atmId);
        ATM atm = optionalATM.get();
        Optional<Card> optionalCard = cardRepository.findByCardNumbeerAndSVVcode(
                userTransferDto.getCardNumbeer(),
                userTransferDto.getSVVcode()
        );
        Card card = optionalCard.get();
        Double summa = userTransferDto.getSumma();
        if (card.getCardType().equals(CardName.VISA)) {

            if (card.isAccountNonExpired()) {
                int count = 0;
                while (count < 3) {
                    if (Objects.equals(card.getPinCode(), userTransferDto.getPinCode())) {

                        int box100 = (int) (summa / 100);
                        int box50 = (int) (summa / 50);
                        int box20 = (int) (summa / 20);
                        int box10 = (int) (summa / 10);
                        int box5 = (int) (summa / 5);

                        for (AtmBoxes atmBox : atm.getAtmBoxes()) {
                            if (
                                    summa % 100 == 0) {
                                atmBox.setUSD100(atmBox.getUSD100() - box100);
                            }
                            if (
                                    summa % 100 == 0
                                            && atmBox.getUSD100() == 0
                                            && summa % box50 == 0
                                            && summa % box50 / 50 > box50
                            ) {
                                atmBox.setUSD50(atmBox.getUSD50() - box50);

                            }
                            if (
                                    summa % 100 == 0
                                            && atmBox.getUSD100() == 0
                                            && summa % box50 == 0
                                            && atmBox.getUSD50() == 0
                                            && atmBox.getUSD10() > box10
                            ) {
                                atmBox.setUSD10(atmBox.getUSD10() - box10);
                            }
                            if (
                                    summa % 100 == 0
                                            && atmBox.getUSD100() < box100
                                            && summa % box50 == 0
                                            && atmBox.getUSD50() < box50
                                            && atmBox.getUSD10() > box10

                            ) {
                                atmBox.setUSD10(atmBox.getUSD10() - box10);
                            }
                            if (
                                    atmBox.getUSD100() == 0
                                            && atmBox.getUSD50() == 0
                                            && atmBox.getUSD10() == 0
                                            && atmBox.getUSD5() == 0
                                            && atmBox.getUSD1() == 0
                                            && atmBox.getUSD20() > box20
                            ) {
                                atmBox.setUSD20(atmBox.getUSD20() - box20);
                            }
                        }

                        double comision = userTransferDto.getSumma() * 0.01;
                        Bank bank = atm.getBank();

                        //AMALIYOT HAQIDA  MALUMOT
                        List<OutputInfo> outputInfoAtm = atm.getOutputInfo();
                        OutputInfo outputInfo=new OutputInfo();
                        outputInfo.setActionDate(new Date());
                        outputInfo.setOutputSumma(userTransferDto.getSumma());
                        outputInfo.setCards(Collections.singletonList(card));
                        outputInfoAtm.add(outputInfo);

                        card.setCardBalance(card.getCardBalance() - userTransferDto.getSumma() - comision);
                        bank.setBankAccountBalance(bank.getBankAccountBalance() + comision);

                        cardRepository.save(card);

                        return ResponseEntity.ok(new ApiResponse("Amaliyot bajarildi",outputInfo,true));


                    } else count++;
                }
                if (count == 3) {
                    card.setAccountNonLocked(false);
                    cardRepository.save(card);
                    return ResponseEntity.status(HttpStatus.LOCKED).body("Pin kodni 3 marta notugri kiritdingiz karta bloklandi!");
                }
            }
        }
        return ResponseEntity.status(404).body("ATM VISA card uchun xizmat qiladi");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Card> optionalCard = cardRepository.findByCardNumbeer(username);
        if (optionalCard.isPresent())
            return optionalCard.get();
        throw new UsernameNotFoundException("Karta mavjud emas");
    }

    public void sendEmail(String sendingEmail, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("sandjarbeek@gmail.com");
            mailMessage.setTo(sendingEmail);
            mailMessage.getReplyTo();
            mailMessage.setSubject("Diqqat ogohlantirish!");
            mailMessage.setText(message);
            javaMailSender.send(mailMessage);


        } catch (Exception e) {
        }
    }

    public void checkBox() {
        List<ATM> allAtm = atmRepository.findAll();
        for (ATM atm : allAtm) {
            for (AtmBoxes atmBox : atm.getAtmBoxes()) {
                Bank bank = atm.getBank();
                Set<User> users = bank.getUser();


                for (User user : users) {
                    //SHU ATMGA TEGISHLIK BANKING ADMINLARIGA XABAR YUBORADI
                    if (user.getRoles().equals(RoleName.ADMIN)) {
                        if (atmBox.getUSD1() < 20) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  1$ qutida" + " 20$ kam mablag' qoldi. " + "Qoldiq: " + atmBox.getUSD1() + "$");
                        }
                        if (atmBox.getUSD5() < 10) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  5$ qutida" + " 50$ kam mablag' qoldi. " + "Qoldiq:" + atmBox.getUSD5() + "$");
                        }
                        if (atmBox.getUSD10() < 10) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  10$ qutida" + " 100$ kam mablag' qoldi. " + "Qoldiq:" + atmBox.getUSD10() + "$");
                        }
                        if (atmBox.getUSD20() < 5) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  20$ qutida" + " 100$ kam mablag' qoldi. " + "Qoldiq:" + atmBox.getUSD20() + "$");
                        }
                        if (atmBox.getUSD50() < 4) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  50$ qutida" + " 200$ kam mablag' qoldi. " + "Qoldiq:" + atmBox.getUSD50() + "$");
                        }
                        if (atmBox.getUSD100() < 4) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  100$ qutida" + " 400$ kam mablag' qoldi. " + "Qoldiq:" + atmBox.getUSD100() + "$");
                        }
                        if (atmBox.getSum1000() < 100) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  1000 UZS qutida" + " 100.000 so'mdan kam mablag' qoldi. " + "Qoldiq:" + atmBox.getSum1000() + " UZS");
                        }
                        if (atmBox.getSum5000() < 20) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  5000 UZS qutida" + " 100.000 so'mdan kam mablag' qoldi. " + "Qoldiq:" + atmBox.getSum5000() + " UZS");
                        }
                        if (atmBox.getSum10000() < 20) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  10.000 UZS qutida" + " 200.000 so'mdan kam mablag' qoldi. " + "Qoldiq:" + atmBox.getSum10000() + " UZS");
                        }
                        if (atmBox.getSum50000() < 10) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  50.000 UZS qutida" + " 500.000 so'mdan kam mablag' qoldi. " + "Qoldiq:" + atmBox.getSum50000() + " UZS");
                        }
                        if (atmBox.getSum10000() < 10) {
                            sendEmail(user.getEmail(), atm.getAddressAtm() + " ATM  100.000 UZS qutida" + " 1.000.000 so'mdan kam mablag' qoldi. " + "Qoldiq:" + atmBox.getSum100000() + " UZS");
                        }

                    }
                }
            }
        }
    }
}
