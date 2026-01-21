import { useRef, useState } from "react";
import type { WalletDTO, WalletType, WalletUpdateDTO } from "../../services/finances/wallet/wallet.dto";
import { InputField } from "../common/InputField";
import { nameRules } from "../../rules/rules";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { NumericField } from "../common/NumericField";
import { CurrencyCode, formatBalance } from "../../services/finances/currencies.type";
import CurrencySelect from "./CurrencySelect";

interface WalletFormProps {
	wallet?: WalletResponseDTO;
	onClose: () => void;
	onCreate?: (exercise: WalletDTO) => Promise<void>;
	onUpdate?: (id: number, data: WalletUpdateDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function WalletForm({ wallet, onClose, onCreate, onUpdate, onDelete }: WalletFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);

	const [name, setName] = useState<string>(wallet?.name || "");
	const [currency, setCurrency] = useState<CurrencyCode>(wallet?.currency || "EUR");
	const [type, setType] = useState<WalletType>(wallet?.type || "BANK");
	const [balance, setBalance] = useState<string>(wallet?.balance.replace(/\D+$/g, "") || "0.00");

	const nameRef = useRef<any>(null);
	const balanceRef = useRef<any>(null);

	const handleUpdate = async () => {
		if (!onUpdate || !wallet) return;
		await onUpdate(wallet.id, { name, type });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !wallet) return;
		await onDelete(wallet.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate) return;
		await onCreate({ name, type, currency, balance });
		onClose();
	};


	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		
		const isNameValid = nameRef.current?.validate();
		const isBalanceValid = balanceRef.current?.validate();
		
		if (!isNameValid || !isBalanceValid) {
			return;
		}
		
		setSubmitting(true);
		if (wallet !== undefined) {
			handleUpdate();
		} else {
			handleCreate();
		}

		setSubmitting(false);
	};

	return (
		<>
			<div className="
			bg-foreground 
			rounded-xl 
			shadow-lg 
			p-12
			flex flex-col
			gap-4
			text-textcolor
			"
			>
				<button
					onClick={onClose}
					className="absolute top-2 right-4 text-white text-xl hover:text-gray-200 p-2 cursor-pointer"
				>
					✕
				</button>

				<form className="flex flex-col space-y-4 w-90 sm:w-110" onSubmit={handleSubmit}>
					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Name
						</label>
						<InputField
							ref={nameRef}
							value={name}
							onChange={setName}
							placeholder="Wallet name"
							rules={nameRules()}
						/>
					</div>

					<div className="flex flex-col">
						<label htmlFor="date" className="text-sm mb-1">
							Currency
						</label>
						<CurrencySelect value={currency} onChange={setCurrency} disabled={!!wallet}/>
					</div>

					<div className="flex flex-row gap-2">

						<div className="flex flex-col w-2/5">
							<label htmlFor="Type" className="text-sm mb-1">
								Type
							</label>
							<div className="flex flex-row ">
								<button
									type="button"
									className={`w-50 p-2 border border-secondary/50 rounded-l-lg ${type === "BANK" ? "bg-primary border-primary" : "bg-background hover:bg-secondary/20"} cursor-pointer`}
									onClick={() => setType("BANK")}
								>
									BANK
								</button>
								<button
									type="button"
									className={`w-50 p-2 border border-secondary/50 rounded-r-lg ${type === "CASH" ? "bg-primary border-primary" : "bg-background hover:bg-secondary/20"} cursor-pointer`}
									onClick={() => setType("CASH")}
								>
									CASH
								</button>
							</div>
						</div>

						<div className="flex flex-col w-3/5">
							<label htmlFor="date" className="text-sm mb-1">
								Balance
							</label>
							<NumericField
								ref={balanceRef}
								value={balance}
								onChange={setBalance}
								formatter={(val) => formatBalance(val)}
								rules={[
									(v: string) => !isNaN(Number(v)) || "Invalid number"
								]}
								placeholder="0.00"
								disabled={!!wallet}
							/>
						</div>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{wallet ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>
				</form>

				{wallet &&
					<div className="border-t border-secondary/50">
						<button
							className="form-submit bg-red-500 w-full hover:bg-red-600"
							onClick={handleDelete}
						>
							Delete Wallet
						</button>
					</div>
				}
			</div>
		</>
	);
}