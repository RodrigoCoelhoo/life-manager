import { useRef, useState } from "react";
import { InputField } from "../common/InputField";
import { descriptionRules } from "../../rules/rules";
import { DateTimeField } from "../common/DateTimeField";
import { ButtonField } from "../common/ButtonField";
import type { WalletResponseDTO, WalletSimpleResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { Modal } from "../common/Modal";
import { walletService } from "../../services/finances/wallet/wallet.service";
import { SearchList } from "../common/SearchList";
import { MagnifyingGlassIcon } from "@heroicons/react/16/solid";
import { NumericField } from "../common/NumericField";
import type { TransferenceDTO, TransferenceResponseDTO } from "../../services/finances/transference/transference.dto";

interface TransferenceFormProps {
	transference?: TransferenceResponseDTO;
	onClose: () => void;
	onCreate?: (transference: TransferenceDTO) => Promise<void>;
	onUpdate?: (id: number, data: TransferenceDTO) => Promise<void>;
	onDelete?: (id: number) => Promise<void>;
}

export default function TransferenceForm({ transference, onClose, onCreate, onUpdate, onDelete }: TransferenceFormProps) {
	const [submitting, setSubmitting] = useState<boolean>(false);
	const [openWalletSearch, setOpenWalletSearch] = useState<boolean>(false);
	const [walletTarget, setWalletTarget] = useState<"from" | "to">("from");

	const [date, setDate] = useState<string>(transference?.date || "");
	const [description, setDescription] = useState<string>(transference?.description || "");
	const [fromWallet, setFromWallet] = useState<WalletSimpleResponseDTO | undefined>(transference?.fromWallet);
	const [toWallet, setToWallet] = useState<WalletSimpleResponseDTO | undefined>(transference?.toWallet);
	const [amount, setAmount] = useState<string>(transference?.fromAmount.slice(0, transference?.fromAmount.length - 1) || "0.00");

	const dateRef = useRef<any>(null);
	const fromWalletRef = useRef<any>(null);
	const toWalletRef = useRef<any>(null);
	const amountRef = useRef<any>(null);
	const descriptionRef = useRef<any>(null);

	const handleUpdate = async () => {
		if (!onUpdate || !transference || fromWallet === undefined || toWallet === undefined) return;
		await onUpdate(transference.id, { date, fromWalletId: fromWallet.id, toWalletId: toWallet.id, amount, description });
		onClose();
	};

	const handleDelete = async () => {
		if (!onDelete || !transference) return;
		await onDelete(transference.id);
		onClose();
	};

	const handleCreate = async () => {
		if (!onCreate || fromWallet === undefined || toWallet === undefined) return;
		await onCreate({ date, fromWalletId: fromWallet.id, toWalletId: toWallet.id, amount, description });
		onClose();
	};


	const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();

		const isDateValid = dateRef.current?.validate();
		const isToWalletValid = toWalletRef.current?.validate();
		const isFromWalletValid = fromWalletRef.current?.validate();
		const isAmountValid = amountRef.current?.validate();
		const isDescriptionValid = descriptionRef.current?.validate();

		if (!isDateValid || !isToWalletValid || !isFromWalletValid || !isAmountValid || !isDescriptionValid || fromWallet?.id == toWallet?.id) {
			return;
		}

		setSubmitting(true);
		if (transference !== undefined) {
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

					<div className="flex flex-row gap-2">
						<div className="flex flex-col w-full">
							<label htmlFor="date" className="text-sm mb-1">
								Date
							</label>
							<DateTimeField
								ref={dateRef}
								type="date"
								value={date}
								onChange={setDate}
								rules={[val => (!!val ? true : "Date is required")]}
							/>
						</div>

						<div className="flex flex-col w-full">
							<label htmlFor="date" className="text-sm mb-1">
								Amount
							</label>
							<NumericField
								ref={amountRef}
								value={amount}
								onChange={setAmount}
								rules={[
									(v: string) => !isNaN(Number(v)) || "Invalid number",
									(v: string) => v.trim() !== "" || "Amount is required",
									(v: string) => Number(v) !== 0.00 || "Amount is required"
								]}
								placeholder="0.00"
							/>
						</div>
					</div>

					<div className="flex flex-row gap-2">
						<div className="flex flex-col w-full">
							<label htmlFor="date" className="text-sm mb-1">
								Sender
							</label>
							<ButtonField
								ref={fromWalletRef}
								value={fromWallet !== undefined ? fromWallet.name : ""}
								onClick={() => {
									setWalletTarget("from");
									setOpenWalletSearch(true);
								}}
								placeholder="Select a wallet"
								icon={<MagnifyingGlassIcon width={16} height={16} />}
								rules={[
									(value: string) => (value.trim() !== "" ? true : "Please select a wallet")
								]}
							/>
						</div>

						<div className="flex flex-col w-full">
							<label htmlFor="date" className="text-sm mb-1">
								Receiver
							</label>
							<ButtonField
								ref={toWalletRef}
								value={toWallet !== undefined ? toWallet.name : ""}
								onClick={() => {
									setWalletTarget("to");
									setOpenWalletSearch(true);
								}}
								placeholder="Select a wallet"
								icon={<MagnifyingGlassIcon width={16} height={16} />}
								rules={[
									(value: string) => (value.trim() !== "" ? true : "Please select a wallet")
								]}
							/>
						</div>
					</div>
					{
						fromWallet !== undefined &&
						toWallet !== undefined &&
						fromWallet?.id === toWallet?.id &&
						<span className="text-red-400">Sender and Receiver wallets can't be equal</span>
					}

					<div>
						<label htmlFor="date" className="text-sm mb-1">
							Description
						</label>
						<InputField
							ref={descriptionRef}
							value={description}
							onChange={setDescription}
							placeholder="Description"
							rules={descriptionRules()}
							multiline={true}
						/>
					</div>

					<button
						type="submit"
						className="form-submit"
						disabled={submitting}
					>
						{transference ? (submitting ? "Saving" : "Save Changes") : (submitting ? "Creating" : "Create")}
					</button>
				</form>

				{transference &&
					<div className="border-t border-secondary/50">
						<button
							className="form-submit bg-red-500 w-full hover:bg-red-600"
							onClick={handleDelete}
						>
							Delete Transaction
						</button>
					</div>
				}
			</div>

			<Modal isOpen={openWalletSearch} onClose={() => setOpenWalletSearch(false)}>
				<SearchList<WalletResponseDTO>
					fetchItems={walletService.getAllWallets}
					onSelect={(wallet: WalletResponseDTO) => {
						if (walletTarget === "from") setFromWallet(wallet);
						else if (walletTarget === "to") setToWallet(wallet);

						setOpenWalletSearch(false);
					}}
					onClose={() => setOpenWalletSearch(false)}
				/>
			</Modal>
		</>
	);
}