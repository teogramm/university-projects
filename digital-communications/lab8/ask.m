inputData = [1 0 1 0 0 1 1 1 0];
t=0:.01:length(inputData);
% Signal matrix
s=zeros(100*length(inputData)+1,1);
for i=1:length(inputData)
    if inputData(i) == 0
        % If bit is 0 signal is 0
        s((i-1)*100+1:(i-1)*100+100) = 0;
    else 
        % Index for signal matrix
        k=(i-1)*100;
        for j=i-1:.01:(i-1)+0.99
            k=k+1;
            s(k)=sin(2*pi*5*t(k));
        end
    end
end
plot(t,s);
xlabel('t');
ylabel('s(t)');
title('ASK of [1 0 1 0 0 1 1 1 0]')

