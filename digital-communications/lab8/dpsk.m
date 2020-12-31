inputData = [1 0 1 0 0 1 1 1 0];
t=0:.01:length(inputData);
% Signal matrix
s=zeros(100*length(inputData)+1,1);
% Set the starting phase to 0
currentPhase = 0;
for i=1:length(inputData)
    % Index for signal matrix
    k=(i-1)*100;
    if inputData(i)==0
        % If bit is 0 we do not change the phase
    else 
        % If bit is different we toggle the phase
        currentPhase = invertPhase(currentPhase);
    end
    for j=i-1:.01:(i-1)+0.99
        k=k+1;
        s(k)=sin(2*pi*1*t(k) + currentPhase);
    end
end
plot(t,s);
xlabel('t');
ylabel('s(t)');
title('DPSK of [1 0 1 0 0 1 1 1 0]')

function newPhase = invertPhase(currPhase)
    if(currPhase == 0)
        newPhase = pi;
    else
        newPhase = 0;
    end
end