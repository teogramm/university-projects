function [avgAEGEAN,avgOPAP] = compare(n)
%   This functions compares the predictions made by least square method
%   of power n to the actual stock prices
    sessions = 0:9;
    pricesA = [7.8800 7.9000 7.8500 7.7600 7.7600 7.9400 7.6900 7.9500 7.9800 7.8600];
    pricesO = [9.4600 9.8000 9.8600 9.8750 9.8000 9.7400 9.6850 9.7800 9.9500 9.9000];
    avgAEGEAN = 0;
    avgOPAP = 0;
    for session = sessions
        avgAEGEAN = avgAEGEAN + abs(ARAIG(session,n)-pricesA(session+1));
        avgOPAP = avgOPAP + abs(OPAP(session,n)-pricesO(session+1));
    end
    avgAEGEAN = avgAEGEAN/10;
    avgOPAP = avgOPAP/10;
end

